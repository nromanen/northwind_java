package model;

public record Product(int id, String name, int supplierId, int categoryId,
                      String quantityPerUnit, float unitPrice, int unitsInStock,
                      int unitsOnOrder, int reorderLevel, int discontinued) {
}
