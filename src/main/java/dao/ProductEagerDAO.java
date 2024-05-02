package dao;

import database.Util;
import model.Category;
import model.Product;
import model.ProductClass;
import model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductEagerDAO {
    private final Util dbUtil = new Util();

    public List<ProductClass> all() throws SQLException {
        Statement st = dbUtil.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select * from categories join products on products.category_id = categories.category_id " +
                "join suppliers on suppliers.supplier_id = products.supplier_id");
        List<ProductClass> result = new ArrayList<>();
        while (rs.next()) {
            result.add(fromResultSet(rs));
        }
        return result;
    }

    public ProductClass byId(Integer id) throws SQLException {
        String query = "select * from categories join products on products.category_id = categories.category_id " +
                "join suppliers on suppliers.supplier_id = products.supplier_id where product_id = ?";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        List<ProductClass> result = new ArrayList<>();
        rs.next();
        return fromResultSet(rs);
    }

    private ProductClass fromResultSet(ResultSet rs) throws SQLException {
        Category category = new Category(rs.getInt("category_id"),
                rs.getString("category_name"),
                rs.getString("description"),
                rs.getBytes("picture"));
        Supplier supplier = new Supplier(rs.getInt("supplier_id"),
                rs.getString("company_name"),
                rs.getString("contact_name"),
                rs.getString("contact_title"),
                rs.getString("address"));
        return new ProductClass(rs.getInt("product_id"),
                rs.getString("product_name"),
                supplier,
                category,
                rs.getString("quantity_per_unit"),
                rs.getFloat("unit_price"),
                rs.getInt("units_in_stock"),
                rs.getInt("units_on_order"),
                rs.getInt("reorder_level"),
                rs.getInt("discontinued"));
    }

    public static void main(String[] args) {
        try {
            System.out.println(new ProductEagerDAO().byId(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

