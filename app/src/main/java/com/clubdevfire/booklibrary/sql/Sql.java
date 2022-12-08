package com.clubdevfire.booklibrary.sql;

import android.app.Activity;
import android.content.Context;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Sql extends Thread {
    private final String mQuery;
    private final Activity mContext;
    private OnQueryCompleteListener onQueryCompleteListener = result -> {};
    private static Connection mConnection;
    
    public Sql(Context context, String query) {
        this.mQuery = query;
        this.mContext = (Activity) context;
    }
    
    @Override
    public void run() {
        super.run();
        try {
            Class.forName("org.postgresql.Driver");
        }
        catch (java.lang.ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        ResultSet result = null;
        try {
            if (mConnection != null) while (true) if (mConnection.isClosed()) break;
            mConnection = DriverManager.getConnection("jdbc:postgresql://motty.db.elephantsql.com:5432/ovprrajp", "ovprrajp", "gHihnGYooqQLeM66CsduCbs_AwFaR49M");
            result = mConnection.createStatement().executeQuery(mQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mConnection != null) mConnection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        ResultSet finalResult = result;
        mContext.runOnUiThread(() -> onQueryCompleteListener.onQueryComplete(finalResult));
    }
    
    public Sql setOnQueryCompleteListener(OnQueryCompleteListener newListener){
        onQueryCompleteListener = newListener;
        return this;
    }
    
    public interface OnQueryCompleteListener {
        void onQueryComplete(ResultSet result);
    }

}

