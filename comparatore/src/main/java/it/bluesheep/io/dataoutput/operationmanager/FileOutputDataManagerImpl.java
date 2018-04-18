package it.bluesheep.io.dataoutput.operationmanager;

import java.util.List;

public abstract class FileOutputDataManagerImpl extends OutputDataManagerImpl {

	@Override
	public abstract void saveRecord(List<Object> recordsToBeSaved);

}
