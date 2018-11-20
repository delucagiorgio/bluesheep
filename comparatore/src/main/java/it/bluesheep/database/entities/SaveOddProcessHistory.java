package it.bluesheep.database.entities;

import java.sql.Timestamp;

import it.bluesheep.comparatore.serviceapi.Service;
import it.bluesheep.database.ProcessStatus;

public class SaveOddProcessHistory extends AbstractBlueSheepEntity {

	private String serviceType;
	private String status;
	private String errorMessage;
	
	public SaveOddProcessHistory(Service service, ProcessStatus status, String errorMessage, long id, Timestamp createTimestamp, Timestamp updateTimestamp) {
		super(id, createTimestamp, updateTimestamp);
		this.serviceType = service.getCode();
		this.status = status.getCode();
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String getTelegramButtonText() {
		return null;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
