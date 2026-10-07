package dataaccess.rdgw;

import business.SaleStatus;
import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class SaleRowDataGateway {

    private int id;
    private Date date;
    private SaleStatus status;
    private int customerId;
    private double totalSale;
    private double totalDiscount;

    private static final String GET_SALE_SQL = "select id, open_date, status, customer_id, total_sale, total_discount " + "from sale where id = ?"; //TODO complete me
    private static final String INSERT_SALE_SQL = "insert into sale " + "(open_date, status, customer_id, total_sale, total_discount) " + "values (?, ?, ?, ?, ?)"; //TODO complete me

    public SaleRowDataGateway(
      Date date,
      SaleStatus status,
      int customerId,
      double totalSale,
      double totalDiscount) {
        this.date = date;
        this.status = status;
        this.customerId = customerId;
        this.totalSale = totalSale;
        this.totalDiscount = totalDiscount;
      }
    
    public int getId() { return id; }
    public SaleStatus getStatus() { return status; }
    public int getCustomerId() { return customerId; }
    public double getDiscount() { return totalDiscount; }

    public void insert() throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepareGetGenKey(INSERT_SALE_SQL)) {
            statement.setDate(1, date);
            statement.setString(2, status.toString());
            statement.setInt(3, customerId);
            statement.setDouble(4, totalSale);
            statement.setDouble(5, totalDiscount);
            statement.executeUpdate();
			try (ResultSet rs = statement.getGeneratedKeys()) {
				rs.next();
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
    }

    public static SaleRowDataGateway find(int id) throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_SQL )) {
			statement.setInt(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				return load(rs);
			}
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting sale by its id", e);
		}
    }

    private static SaleRowDataGateway load(ResultSet rs) throws RecordNotFoundException {
		try {
			if (!rs.next()) {
        throw new RecordNotFoundException("Sale does not exist");
      }
			SaleRowDataGateway newSale = new SaleRowDataGateway(rs.getDate("open_date"),
					SaleStatus.valueOf(rs.getString("status")), rs.getInt("customer_id"),
                    rs.getDouble("total_sale"), rs.getDouble("total_discount"));
			newSale.id = rs.getInt("id");
			return newSale;
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Sale does not exist", e);
		}
    }
}