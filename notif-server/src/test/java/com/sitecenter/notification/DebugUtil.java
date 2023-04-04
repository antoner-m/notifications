package com.sitecenter.notification;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DebugUtil {
    public static void printQuery(JdbcTemplate jdbc, String query, Object ... params) {
        System.out.println(query);
        jdbc.query(query, params, new RowMapper<Object>() {
            private List<String> columnNames;

            @Override
            public Object mapRow(ResultSet rs, int i) throws SQLException {
                loadColumnNames(rs);
                System.out.println("  row: " + i);
                for(String name : columnNames) {
                    Object value = rs.getObject(name);
                    System.out.println("    " + name + ": " + value);
                }
                return null;
            }

            private void loadColumnNames(ResultSet rs) throws SQLException {
                if(columnNames != null) {
                    return;
                }
                columnNames = new ArrayList<>();
                ResultSetMetaData metadata = rs.getMetaData();
                for(int col = 1; col <= metadata.getColumnCount(); col ++) {
                    columnNames.add(metadata.getColumnName(col));
                }
            }
        });
    }

}
