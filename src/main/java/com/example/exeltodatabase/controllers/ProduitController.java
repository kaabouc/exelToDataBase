package com.example.exeltodatabase.controllers;

import com.example.exeltodatabase.domain.Produit;
import com.example.exeltodatabase.services.ProduitService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;


@RestController
public class ProduitController {

    @Autowired
    private ProduitService productService;

    @PostMapping("/api/products/upload")
    public ResponseEntity<byte[]> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            List<Produit> produits = productService.readExcelFile(file);
            // Save products to the database
            productService.saveProduitsFromExcel(file);

            // Process the products and create a new Excel workbook with status columns
            Workbook resultWorkbook = createResultWorkbook(produits);

            // Convert the workbook to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            resultWorkbook.write(byteArrayOutputStream);

            // Close the workbook before returning the response
            resultWorkbook.close();

            // Prepare the response
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "result.xlsx");

            return new ResponseEntity<>(byteArrayOutputStream.toByteArray(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error occurred while processing the file: " + e.getMessage()).getBytes());
        }
    }

    public Workbook createResultWorkbook(List<Produit> produits) {
        Workbook workbook = new XSSFWorkbook();

        // Create sheet for product data
        Sheet dataSheet = workbook.createSheet("Data");
        createDataSheetHeader(dataSheet);
        fillDataSheet(dataSheet, produits);

        // Create sheet for insertion results
        Sheet resultSheet = workbook.createSheet("InsertionResults");
        createResultSheetHeader(resultSheet);
        fillResultSheet(resultSheet, produits);

        return workbook;
    }

    private void createResultSheetHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Insertion Result");
    }

    private void fillResultSheet(Sheet sheet, List<Produit> produits) {
        for (int i = 0; i < produits.size(); i++) {
            Produit produit = produits.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(produit.getId());

            // Add a result column based on the insertion status
            Cell resultCell = row.createCell(1);
            if (produit.getId() != null) {
                resultCell.setCellValue("Success");
            } else {
                resultCell.setCellValue("Failed");
            }
        }
    }


    private void createDataSheetHeader(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("ID");
        headerRow.createCell(1).setCellValue("Nom");
        headerRow.createCell(2).setCellValue("Description");
        headerRow.createCell(3).setCellValue("Prix");
    }

    private void fillDataSheet(Sheet sheet, List<Produit> produits) {
        for (int i = 0; i < produits.size(); i++) {
            Produit produit = produits.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(produit.getId());
            row.createCell(1).setCellValue(produit.getNom());
            row.createCell(2).setCellValue(produit.getDescription());
            row.createCell(3).setCellValue(produit.getPrix().doubleValue());
        }
    }




}