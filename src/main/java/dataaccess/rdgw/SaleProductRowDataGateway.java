package dataaccess.rdgw;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import dataaccess.DataSource;
import dataaccess.exception.PersistenceException;
import dataaccess.exception.RecordNotFoundException;

public class SaleProductRowDataGateway {

    private int saleId;
    private int productId;
    private double qty;

    private static final String INSERT_PRODUCT_SALE_SQL = "insert into sale_product (sale_id, product_id, qty) " + "values (?, ?, ?)"; //TODO complete me
    private static final String GET_SALE_PRODUCTS_SQL = "select sale_id, product_id, qty " + "from sale_product " + "where sale_id = ?"; //TODO complete me

	// Construtor
	public SaleProductRowDataGateway(
	int saleId,
	int productId,
	double qty) {
		this.saleId = saleId;
		this.productId = productId;
		this.qty = qty;
	}

    public int getProductId() { return productId; }
    public double getQty() { return qty; }

    public void insert() throws PersistenceException {
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(INSERT_PRODUCT_SALE_SQL)) {
			statement.setInt(1, saleId);
			statement.setInt(2, productId);
			statement.setDouble(3, qty);
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException ("Internal error!", e);
		}
    }

    public static Set<SaleProductRowDataGateway> findSaleProducts(int saleId) throws PersistenceException {
		Set<SaleProductRowDataGateway> result = new HashSet<>();
		try (PreparedStatement statement = DataSource.INSTANCE.prepare(GET_SALE_PRODUCTS_SQL)) {
			statement.setInt(1, saleId);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					result.add(
						new SaleProductRowDataGateway(
						rs.getInt("sale_id"),
						rs.getInt("product_id"),
						rs.getDouble("qty"))
					);
				}
			}
			return result;
		} catch (SQLException e) {
			throw new PersistenceException("Internal error getting sale product by its id", e);
		}
    }

    private static SaleProductRowDataGateway load(ResultSet rs) throws RecordNotFoundException {
		try {
			if (!rs.next()) {
                throw new RecordNotFoundException("Sale Product does not exist");
            }
			SaleProductRowDataGateway newSaleProduct = new SaleProductRowDataGateway(
				rs.getInt("sale_id"),
				rs.getInt("product_id"),
				rs.getDouble("qty")
			);
			return newSaleProduct;
		} catch (SQLException e) {
			throw new RecordNotFoundException ("Sale Product does not exist", e);
		}
	}
}