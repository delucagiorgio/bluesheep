package it.bluesheep.database.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import it.bluesheep.comparatore.entities.output.RecordOutput;
import it.bluesheep.comparatore.entities.output.subtype.RecordBookmakerVsBookmakerOdds;
import it.bluesheep.database.dao.IOddDAO;
import it.bluesheep.database.entities.PPOdd;
import it.bluesheep.util.BlueSheepConstants;

public class PPOddDAO extends AbstractOddDAO<PPOdd> implements IOddDAO<PPOdd> {

	private static PPOddDAO instance;
	public static final String tableName = "PP_ODDS";
	private static final String DATA = "dataOraEvento";
	private static final String SPORT = "sport";
	private static final String EVENTO = "evento";
	private static final String CAMPIONATO = "campionato";
	private static final String RATING1 = "rating1";
	private static final String RATING2 = "rating2";
	private static final String BOOKMAKERNAME1 = "bookmakerName1";
	private static final String SCOMMESSABOOK1 = "scommessaBookmaker1";
	private static final String QUOTABOOK1 = "quotaScommessaBookmaker1";
	private static final String BOOKMAKERNAME2 = "bookmakerName2";
	private static final String SCOMMESSABOOK2 = "scommessaBookmaker2";
	private static final String QUOTABOOK2 = "quotaScommessaBookmaker2";
	private static final String NAZIONE = "nazione";
	private static final String LINKBOOK1 = "linkBook1";
	private static final String LINKBOOK2 = "linkBook2";
	private static final String NETPROFIT = "netProfit";
	private static final String LIQUIDITA1 = "liquidita1";
	private static final String LIQUIDITA2 = "liquidita2";
	
	protected PPOddDAO(Connection connection) {
		super(tableName, connection);
	}
	
	public static synchronized PPOddDAO getPPOddDAOInstance(Connection connection) {
		if(instance == null) {
			instance = new PPOddDAO(connection);
		}
		
		return instance;
	}

	@Override
	protected List<PPOdd> mapDataIntoObject(ResultSet returnSelect) throws SQLException {
		
		List<PPOdd> ppOddList = new ArrayList<PPOdd>(returnSelect.getFetchSize());
		
		while(returnSelect.next()) {
			Timestamp dataOraEvento = getTimestampFromResultSet(returnSelect, DATA);
			String sport = returnSelect.getString(SPORT);
			String evento = returnSelect.getString(EVENTO);
			String campionato = returnSelect.getString(CAMPIONATO);
			Double rating1 = returnSelect.getDouble(RATING1) == 0 ? null : returnSelect.getDouble(RATING1);
			Double rating2 = returnSelect.getDouble(RATING2) == 0 ? null : returnSelect.getDouble(RATING2);;
			String bookmakerName1 = returnSelect.getString(BOOKMAKERNAME1);
			String scommessaBookmaker1 = returnSelect.getString(SCOMMESSABOOK1);
			Double quotaScommessaBookmaker1 = returnSelect.getDouble(QUOTABOOK1) == 0 ? null : returnSelect.getDouble(QUOTABOOK1);
			String bookmakerName2 = returnSelect.getString(BOOKMAKERNAME2);
			String scommessaBookmaker2 = returnSelect.getString(SCOMMESSABOOK2);
			Double quotaScommessaBookmaker2 = returnSelect.getDouble(QUOTABOOK2) == 0 ? null : returnSelect.getDouble(QUOTABOOK2);
			String nazione = returnSelect.getString(NAZIONE);
			String linkBook1 = returnSelect.getString(LINKBOOK1);
			String linkBook2 = returnSelect.getString(LINKBOOK2);
			Double netProfit = returnSelect.getDouble(NETPROFIT) == 0 ? null : returnSelect.getDouble(NETPROFIT);
			Double liquidita1 = returnSelect.getDouble(LIQUIDITA1) == 0 ? null : returnSelect.getDouble(LIQUIDITA1);;
			Double liquidita2 = returnSelect.getDouble(LIQUIDITA2) == 0 ? null : returnSelect.getDouble(LIQUIDITA2);;
			long id = returnSelect.getLong(ID);
			Timestamp createTime = getTimestampFromResultSet(returnSelect, CREATETIME);
			Timestamp updateTime = getTimestampFromResultSet(returnSelect, UPDATETIME);
			
			ppOddList.add(new PPOdd(dataOraEvento, sport, evento, campionato, rating1, rating2, 
					bookmakerName1, scommessaBookmaker1, quotaScommessaBookmaker1, bookmakerName2, 
					scommessaBookmaker2, quotaScommessaBookmaker2, nazione, linkBook1, linkBook2, 
					netProfit, liquidita1, liquidita2, id, createTime, updateTime));
			
		}
		return ppOddList;
	}

	@Override
	protected String getAllColumnValuesFromEntity(PPOdd entity) {
		return "("
				+ DATA + BlueSheepConstants.REGEX_COMMA
				+ SPORT + BlueSheepConstants.REGEX_COMMA
				+ EVENTO + BlueSheepConstants.REGEX_COMMA
				+ CAMPIONATO + BlueSheepConstants.REGEX_COMMA
				+ RATING1 + BlueSheepConstants.REGEX_COMMA
				+ RATING2 + BlueSheepConstants.REGEX_COMMA
				+ BOOKMAKERNAME1 + BlueSheepConstants.REGEX_COMMA
				+ SCOMMESSABOOK1 + BlueSheepConstants.REGEX_COMMA 
				+ QUOTABOOK1 + BlueSheepConstants.REGEX_COMMA
				+ BOOKMAKERNAME2 + BlueSheepConstants.REGEX_COMMA
				+ SCOMMESSABOOK2 + BlueSheepConstants.REGEX_COMMA
				+ QUOTABOOK2 + BlueSheepConstants.REGEX_COMMA
				+ NAZIONE + BlueSheepConstants.REGEX_COMMA
				+ LINKBOOK1 + BlueSheepConstants.REGEX_COMMA
				+ LINKBOOK2 + BlueSheepConstants.REGEX_COMMA
				+ NETPROFIT + BlueSheepConstants.REGEX_COMMA
				+ LIQUIDITA1 + BlueSheepConstants.REGEX_COMMA
				+ LIQUIDITA2 + BlueSheepConstants.REGEX_COMMA +
				"?" + BlueSheepConstants.REGEX_COMMA +
				"?" +")";
	}
	
	protected String getAllColumnValuesFromEntityMultipleInsert() {
		return 	"("
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA 
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?" + BlueSheepConstants.REGEX_COMMA
				+ "?)";
	}

	@Override
	public boolean checkEmptyTable() throws SQLException {
		String query = "select NOT EXISTS (select id from " + tableName + " ) as emptyTable";

		Statement ps = connection.createStatement();
		
		ResultSet rs = ps.executeQuery(query);
		
		if(rs.next()) {
			return rs.getBoolean("emptyTable");
		}
		
		return false;
	}

	@Override
	public void deleteTable() throws SQLException {
		String query = DELETE + tableName;

		Statement ps = connection.createStatement();
		
		ps.executeUpdate(query);
	}

	@Override
	public void insertMultipleRows(List<RecordOutput> recordOutput) throws SQLException {
		
		int countEntity = recordOutput.size();
		
		int i = 0;
		int page = 50;
		do {
			logger.debug("Executing query page " + (i + 1) + " of " + (countEntity / page + (countEntity % page == 0 ? 0 : 1)));
			int startIndex = i * page;
			int endIndex =  startIndex + page;
			List<RecordOutput> subList = recordOutput.subList(startIndex, Math.min(countEntity, endIndex));
			String query = getinsertBaseTableNameQuery() + getAllColumnValuesFromEntityMultipleInsert();
			PreparedStatement ps = connection.prepareStatement(query);
			
			for(RecordOutput entity : subList) {
				ps.setTimestamp(1, new Timestamp(entity.getDataOraEvento().getTime()));
				ps.setString(2, entity.getSport());
				ps.setString(3, entity.getEvento());
				ps.setString(4, entity.getCampionato());
				ps.setDouble(5, entity.getRating());
				ps.setDouble(6, ((RecordBookmakerVsBookmakerOdds) entity).getRating2());
				ps.setString(7, entity.getBookmakerName1());
				ps.setString(8, entity.getScommessaBookmaker1());
				ps.setDouble(9, entity.getQuotaScommessaBookmaker1());
				ps.setString(10, entity.getBookmakerName2());
				ps.setString(11, entity.getScommessaBookmaker2());
				ps.setDouble(12, entity.getQuotaScommessaBookmaker2());
				ps.setString(13, entity.getNazione());
				ps.setString(14, entity.getLinkBook1());
				ps.setString(15, entity.getLinkBook2());
				ps.setDouble(16, entity.getNetProfit());
				ps.setDouble(17, entity.getLiquidita1());
				ps.setDouble(18, entity.getLiquidita2());
				ps.setTimestamp(19, new Timestamp(System.currentTimeMillis()));
				ps.setTimestamp(20, null);
				
				ps.addBatch();
			}
		
			ps.executeBatch();
			i++;
		}while(i * page < countEntity);
		connection.commit();
	}
	
	public List<PPOdd> getPPOddListFromBookmakerList(List<String> bookmakerList) throws SQLException{
		
		String query = getBasicSelectQuery() + WHERE + BOOKMAKERNAME1 + " in (";
		String[] parameter = new String[bookmakerList.size()];
		bookmakerList.toArray(parameter);
		String temp = "";

		int i;
		for(i = 0; i < parameter.length; i++) {
		  temp += ", '" + parameter[i] + "'";
		}

		temp = temp.replaceFirst(",", "");
		temp += ")";
		query = query + temp + OR +  BOOKMAKERNAME2 + " in (" + temp;
		PreparedStatement ps = connection.prepareStatement(query);
		List<PPOdd> ppOddsList = getMappedObjectBySelect(ps);
		
		return ppOddsList;
	}

}
