package dao;

import database.Util;
import model.Category;
import model.Product;
import model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    private final Util dbUtil = new Util();

    public List<Product> all() throws SQLException {
        Statement st = dbUtil.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select * from products");
        List<Product> result = new ArrayList<>();
        while (rs.next()) {
            result.add(fromResultSet(rs));
        }
        return result;
    }


    public Product byId(Integer id) throws SQLException {
        String query = "select * from products where product_id = ?";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        rs.next();
        return fromResultSet(rs);
    }

    public boolean save(Product product, Category category, Supplier supplier) throws SQLException {
        String insert = "insert into products (category_id, supplier_id, product_id, product_name, discontinued, " +
                "units_on_order, units_in_stock, quantity_per_unit, unit_price, reorder_level) " +
                "values (?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(insert);
        st.setInt(1, category.id());
        st.setInt(2, supplier.id());
        st.setInt(3, product.id());
        st.setString(4, product.name());
        st.setInt(5, product.discontinued());
        st.setInt(6, 0);
        st.setInt(7, product.unitsInStock());
        st.setString(8, product.quantityPerUnit());
        st.setFloat(9, product.unitPrice());
        st.setInt(10, product.reorderLevel());
        return st.executeUpdate() == 1;
    }

    /**
     *
     * @param product
     * We can edit just price, count in orders, count in stock and discontinued value
     * @return
     * @throws SQLException
     */
    public boolean edit(Product product) throws SQLException {
        String insert = "update products set discontinued = ?, units_on_order = ?, units_in_stock = ?, unit_price = ? where product_id = ?;";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(insert);
        st.setInt(1, product.discontinued());
        st.setInt(2, product.unitsOnOrder());
        st.setInt(3, product.unitsInStock());
        st.setFloat(4, product.unitPrice());
        st.setInt(5, product.id());
        return st.executeUpdate() == 1;
    }

    public boolean delete(Product product) throws SQLException {
        String insert = "delete from products where product_id = ?;";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(insert);
        st.setInt(1, product.id());
        return st.executeUpdate() == 1;
    }


    private Product fromResultSet(ResultSet rs) throws SQLException {
        return new Product(rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getInt("supplier_id"),
                rs.getInt("category_id"),
                rs.getString("quantity_per_unit"),
                rs.getFloat("unit_price"),
                rs.getInt("units_in_stock"),
                rs.getInt("units_on_order"),
                rs.getInt("reorder_level"),
                rs.getInt("discontinued"));
    }


    public static void main(String[] args) {
        try {
            ProductDAO dao = new ProductDAO();
            Product product = dao.byId(2010);
//            var newProduct = new Product(product.id(), product.name(), product.supplierId(), product.categoryId(),
//                    product.quantityPerUnit(), 1, product.unitsInStock(),
//                    product.unitsOnOrder(), product.reorderLevel(), product.discontinued());
//            dao.edit(newProduct);
//            Product product = dao.byId(1);
//            var newProduct = new Product(2010, "From Java", product.supplierId(), product.categoryId(), product.quantityPerUnit(), product.unitPrice(), product.unitsInStock(),
//                    product.unitsOnOrder(), product.reorderLevel(), product.discontinued());
//            dao.save(newProduct, new Category(1, "", "", null), new Supplier(1,"","","", ""));
            dao.delete(product);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

