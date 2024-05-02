package model;

public record ProductClass(int id, String name, Supplier supplier, Category category,
                      String quantityPerUnit, float unitPrice, int unitsInStock,
                      int unitsOnOrder, int reorderLevel, int discontinued) {
}