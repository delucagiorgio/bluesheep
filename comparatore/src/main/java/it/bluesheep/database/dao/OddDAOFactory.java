package it.bluesheep.database.dao;

import java.sql.Connection;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.dao.impl.PBOddDAO;
import it.bluesheep.database.dao.impl.PPOddDAO;
import it.bluesheep.database.entities.AbstractOddEntity;

public class OddDAOFactory {
	
	private OddDAOFactory() {}
	
	public static IOddDAO<? extends AbstractOddEntity> getCorrectDAOByService(Service service, Connection connection){
		if(Service.BETFAIR_SERVICENAME.equals(service)) {
			return PBOddDAO.getPBOddDAOInstance(connection);
		}else if(Service.TXODDS_SERVICENAME.equals(service)) {
			return PPOddDAO.getPPOddDAOInstance(connection);
		}
		return null;
	}

}
