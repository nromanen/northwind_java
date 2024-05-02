package dao;

import database.Util;
import model.Category;
import model.Supplier;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    private final Util dbUtil = new Util();

    public List<Supplier> all() throws SQLException {
        Statement st = dbUtil.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select supplier_id, company_name, contact_name, contact_title, address from suppliers");
        List<Supplier> result = new ArrayList<>();
        while (rs.next()) {
            result.add(fromResultSet(rs));
        }
        return result;
    }


    public Supplier byId(Integer id) throws SQLException {
        String query = "select * from suppliers where supplier_id = ?";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        List<Category> result = new ArrayList<>();
        rs.next();
        return fromResultSet(rs);
    }


    private Supplier fromResultSet(ResultSet rs) throws SQLException {
        return new Supplier(rs.getInt("supplier_id"),
                rs.getString("company_name"),
                rs.getString("contact_name"),
                rs.getString("contact_title"),
                rs.getString("address"));
    }

    public static void main(String[] args) {
        try {
            System.out.println(new SupplierDAO().byId(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

