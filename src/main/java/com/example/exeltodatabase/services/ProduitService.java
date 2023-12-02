package com.example.exeltodatabase.services;

import com.example.exeltodatabase.domain.Produit;
import com.example.exeltodatabase.repositories.ProduitRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProduitService {
    @Autowired
    private ProduitRepository productRepository;

    public void saveProduit(Produit product) {
        productRepository.save(product);
    }

    public void saveProduitsFromExcel(MultipartFile file) throws IOException {
        List<Produit> products = readExcelFile(file);
        productRepository.saveAll(products);
    }

    public List<Produit> readExcelFile(MultipartFile file) throws IOException {
        List<Produit> produits = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                continue; // Skip header row
            }

            // Extracting ID from the first column (Assuming ID is a numeric value)
            Cell idCell = row.getCell(0);
            Long id = (idCell != null && idCell.getCellType() == CellType.NUMERIC)
                    ? (long) idCell.getNumericCellValue()
                    : null;

            // Extracting other data
            Cell nomCell = row.getCell(1);
            String nom = (nomCell != null) ? nomCell.getStringCellValue() : "";

            Cell descriptionCell = row.getCell(2);
            String description = (descriptionCell != null) ? descriptionCell.getStringCellValue() : "";

            Cell prixCell = row.getCell(3);
            BigDecimal prix = (prixCell != null && prixCell.getCellType() == CellType.NUMERIC)
                    ? new BigDecimal(prixCell.getNumericCellValue())
                    : BigDecimal.ZERO;

            // Creating Produit object
            Produit produit = new Produit();
            produit.setId(id); // Set the manually extracted ID
            produit.setNom(nom);
            produit.setDescription(description);
            produit.setPrix(prix);

            produits.add(produit);
        }

        workbook.close();
        return produits;
    }



}
