package it.bluesheep.entities.util;

public class ComparatoreConstants {
	//general
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String BET365_BOOKMAKER_NAME = "Bet365";
	public static final String BETFAIR_EXCHANGE_BOOKMAKER_NAME = "Betfair Exchange";
	public static final String REGEX_COMMERCIAL_E = "&";
	public static final String REGEX_SLASH = "/";
	public static final String REGEX_PIPE = "|";
	public static final String REGEX_VERSUS = " vs ";
	public static final String REGEX_CSV = ";";
	public static final String REGEX_SPACE = " ";
	public static final String REGEX_MINUS = " - ";
	
	//log
	public static final String LOGGING_PATH = "LOGGING_PATH";
	public static final String LOG_PREFIX_FILENAME = "LOG_PREFIX_FILENAME";
	public static final String LOGGING_MODE_HANDLER = "LOGGING_MODE";
	public static final String LOG_CONSOLE = "CONSOLE";
	public static final String LOG_FILE_OUTPUT = "FILE_OUTPUT";
	public static final String LOGGING_LEVEL_OUTPUT = "LOGGING_LEVEL";
	
	//json
	public static final String JSON_PP_RESULT_PATH = "PATH_OUTPUT_TABLE2";
	public static final String JSON_PB_RESULT_PATH = "PATH_OUTPUT_TABLE1";

	
	//comparison
	public static final String PB_MIN_THRESHOLD = "BETFAIR_MIN_THRESHOLD";
	public static final String PB_MAX_THRESHOLD = "BETFAIR_MAX_THRESHOLD";
	public static final String PP_MIN_THRESHOLD = "TXODDS_MIN_THRESHOLD";
	public static final String PP_MAX_THRESHOLD = "TXODDS_MAX_THRESHOLD";
	public static final String UPDATE_FREQUENCY = "UPDATE_FREQUENCY";
	public static final String MINIMUM_ODD_VALUE = "MINIMUM_ODD_VALUE";

	
	//service names
	public static final String TX_ODDS_SERVICENAME = "TX_ODDS";
	public static final String BETFAIR_SERVICENAME = "BETFAIR";

	//betfair
	public static final String BETFAIR_ENDPOINT = "SPORTS_APING_V1_0";
	public static final String BETFAIR_CERTIFICATE_PATH = "BETFAIR_CERTIFICATE_PATH";
	public static final String BETFAIR_PASSWORD = "BETFAIR_PASSWORD";
	public static final String BETFAIR_USERNAME = "BETFAIR_USER";
	public static final String BETFAIR_APPKEY = "APPKEY";
	public static final String BETFAIR_APPLICATION_JSON = "APPLICATION_JSON";
	public static final String BETFAIR_SESSION_TOKEN_STRING = "sessionToken";
	public static final String BETFAIR_TIMEOUT = "TIMEOUT";
	public static final String BETFAIR_BASE_URL = "BETFAIR_URL";
	public static final String BETFAIR_RESCRIPT_SUFFIX = "RESCRIPT_SUFFIX";
	
	//bet365
	public static final String BET365_TOKEN = "BET365_TOKEN";
	public static final String BET365_DAYS_INTERVAL = "BET365_DAYS";
	
	//txodds
	public static final String TXODDS_DAYS_INTERVAL = "TXODDS_DAYS";
	public static final String TXODDS_USER = "TXODDS_USER";
	public static final String TXODDS_PASSWORD = "TXODDS_PASSWORD";
	
	//translation
	public static final String NATION_PATH_INPUTFILE= "PATH_NAZIONI_TRADUZIONE_CSV";
	public static final String TRANSLATION_PATH_INPUTFILE= "TRANSLATION_PATH_INPUTFILE_CSV";
	
	//csv
	public static final String CSV_ODDS_PATH_INPUT_FILE = "PATH_INPUT_FILE";
	public static final String CSV_PLAYER_RENAME_FILE = "CSV_PLAYER_RENAME_FILE";
	
	private ComparatoreConstants() {}

}
