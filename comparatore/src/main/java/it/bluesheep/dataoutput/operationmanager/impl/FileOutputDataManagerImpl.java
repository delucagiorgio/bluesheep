package it.bluesheep.dataoutput.operationmanager.impl;

import java.util.List;

import it.bluesheep.dataoutput.operationmanager.OutputDataManagerImpl;

public abstract class FileOutputDataManagerImpl extends OutputDataManagerImpl {

	@Override
	public abstract void saveRecord(List<Object> recordsToBeSaved);

}
