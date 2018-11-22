package it.bluesheep.database.dao;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.dao.impl.PBOddDAO;
import it.bluesheep.database.dao.impl.PPOddDAO;
import it.bluesheep.database.entities.AbstractOddEntity;

public class OddDAOFactory {
	
	private OddDAOFactory() {}
	
	public static IOddDAO<? extends AbstractOddEntity> getCorrectDAOByService(Service service){
		if(Service.BETFAIR_SERVICENAME.equals(service)) {
			return PBOddDAO.getPBOddDAOInstance();
		}else if(Service.TXODDS_SERVICENAME.equals(service)) {
			return PPOddDAO.getPPOddDAOInstance();
		}
		return null;
	}

}
