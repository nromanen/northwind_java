package dao;

import database.Util;
import model.Category;
import model.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {
    private final Util dbUtil = new Util();

    public List<Category> all() throws SQLException {
        Statement st = dbUtil.getConnection().createStatement();
        ResultSet rs = st.executeQuery("select * from categories");
        List<Category> result = new ArrayList<>();
        while (rs.next()) {
            result.add(fromResultSet(rs));
        }
        return result;
    }


    public Category byId(Integer id) throws SQLException {
        String query = "select * from categories where category_id = ?";
        PreparedStatement st = dbUtil.getConnection().prepareStatement(query);
        st.setInt(1, id);
        ResultSet rs = st.executeQuery();
        List<Category> result = new ArrayList<>();
        rs.next();
        return fromResultSet(rs);
    }


    private Category fromResultSet(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("category_id"),
                rs.getString("category_name"),
                rs.getString("description"),
                rs.getBytes("picture"));
    }

    public static void main(String[] args) {
        try {
            System.out.println(new CategoryDAO().byId(1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

