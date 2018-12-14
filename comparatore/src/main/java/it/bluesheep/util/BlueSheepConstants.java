package it.bluesheep.util;

public class BlueSheepConstants {
	//general
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String BET365_BOOKMAKER_NAME = "Bet365";
	public static final String BETFAIR_EXCHANGE_BOOKMAKER_NAME_LAY = "Betfair Exchange Banco";
	public static final String BETFAIR_EXCHANGE_BOOKMAKER_NAME_BACK = "Betfair Exchange Punta";
	public static final String BETFLAG_EXCHANGE_BOOKMAKER_NAME = "Betflag Exchange";
	public static final String REGEX_COMMERCIAL_E = "&";
	public static final String REGEX_SLASH = "/";
	public static final String REGEX_PIPE = "|";
	public static final String REGEX_VERSUS = " vs ";
	public static final String REGEX_CSV = ";";
	public static final String REGEX_COMMA = ",";
	public static final String REGEX_SPACE = " ";
	public static final String REGEX_MINUS = " - ";
	public static final String REGEX_TWOPOINTS = ":";
	public static final String KEY_SEPARATOR = "BS_KEY";
	public static final String PROPERTIES_FILENAME = "bluesheepComparatore.properties";
	public static final String TRADUZIONI_NAZIONI_FILENAME = "Country-Nazione_Code.csv";
	public static final String CSV_FILENAME = "InputCsv.csv";
	public static final String BOOKMAKER_LINK_FILENAME = "LinkBookmaker.csv";
	public static final String SERVICE_NAME_LIST = "ACTIVE_SERVICE_NAME";
	public static final String PATH_TO_RESOURCES = "../RISORSE_BLUESHEEP/";
	public static final String BLUESHEEP_APP_STATUS = "bluesheepStatusOn.txt";
	public static final String TRADUZIONI_API_FILENAME = "translationCsv.csv";
	public static final String RENAME_PLAYER_FILENAME = "renamePlayer.csv";
	public static final String FILE_USER_TELEGRAM = "telegramUsersInfo.csv";
	public static final String STATUS1_ARBS_RECORD = "1";
	public static final String STATUS0_ARBS_RECORD = "0";
	public static final String STATUSINVALID_ARBS_RECORD = "-1";
	public static final String BLOCKED_BOOKMAKER_BONUS_ABUSING = "BLOCKED_BOOKMAKER_BONUS_ABUSING";
	public static final String BLOCKED_BOOKMAKER_SUREBET = "BLOCKED_BOOKMAKER_SUREBET";
	
	//json
	public static final String JSON_PP_RESULT_PATH = "PATH_OUTPUT_TABLE2";
	public static final String JSON_PB_RESULT_PATH = "PATH_OUTPUT_TABLE1";
	public static final String FREQ_JSON_SEC = "FREQ_JSON_SEC";
	
	//comparison
	public static final String BONUS_ABUSING_PB_MIN_THRESHOLD = "BONUS_ABUSING_BETFAIR_MIN_THRESHOLD";
	public static final String BONUS_ABUSING_PB_MAX_THRESHOLD = "BONUS_ABUSING_BETFAIR_MAX_THRESHOLD";
	public static final String BONUS_ABUSING_PP_MIN_THRESHOLD = "BONUS_ABUSING_TXODDS_MIN_THRESHOLD";
	public static final String BONUS_ABUSING_PP_MAX_THRESHOLD = "BONUS_ABUSING_TXODDS_MAX_THRESHOLD";
	public static final String UPDATE_FREQUENCY = "UPDATE_FREQUENCY";
	public static final String MINIMUM_ODD_VALUE = "MINIMUM_ODD_VALUE";
	public static final String PP_MIN = "PP_MIN";
	public static final String PP_MAX = "PP_MAX";
	public static final String PB_MIN = "PB_MIN";
	public static final String PB_MAX = "PB_MAX";
	public static final String MINOR_CATEGORY_ONOFF = "MINOR_CATEGORY_ONOFF";
	
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
	public static final String FREQ_BETFAIR_SEC = "FREQ_BETFAIR_SEC";
	
	//bet365
	public static final String BET365_TOKEN = "BET365_TOKEN";
	public static final String BET365_DAYS_INTERVAL = "BET365_DAYS";
	public static final String FREQ_BET365_SEC = "FREQ_BET365_SEC";
	
	//txodds
	public static final String TXODDS_DAYS_INTERVAL = "TXODDS_DAYS";
	public static final String TXODDS_USER = "TXODDS_USER";
	public static final String TXODDS_PASSWORD = "TXODDS_PASSWORD";
	public static final String FREQ_TXODDS_SEC = "FREQ_TXODDS_SEC";
	public static final String FREQ_EVERYMATRIX_SEC = "FREQ_EVERYMATRIX_SEC";
	public static final String FREQ_GOLDBET_SEC = "FREQ_GOLDBET_SEC";
	public static final String FREQ_BETFLAG_SEC = "FREQ_BETFLAG_SEC";
	public static final String FREQ_STARVEGAS_SEC = "FREQ_STARVEGAS_SEC";
	public static final String FREQ_PINTERBET_SEC = "FREQ_PINTERBET_SEC";
	
	//translation
	public static final String NATION_PATH_INPUTFILE = "PATH_NAZIONI_TRADUZIONE_CSV";
	public static final String TRANSLATION_PATH_INPUTFILE = "TRANSLATION_PATH_INPUTFILE_CSV";
	
	//link bookmaker
	public static final String LINK_BOOKMAKER_FILE_PATH = "LINK_BOOKMAKER_FILENAME";
	
	//csv
	public static final String PATH_INPUT_FILE = "PATH_INPUT_FILE";
	public static final String FREQ_CSV_SEC = "FREQ_CSV_SEC";
	public static final String CSV_PLAYER_RENAME_FILE = "CSV_PLAYER_RENAME_FILE";
	public static final String SCRAPED_JSON_PATH = "SCRAPED_JSON_PATH";
	public static final String SCRAPED_JSON_FILENAME = "SCRAPED_JSON_FILENAME";
	
	//arbitraggi
	public static final String RATING1 = "R1";
	public static final String RATING2 = "R2";
	public static final String FILENAME_PREVIOUS_RUNS_2WAY = "previousRunFile_2w.txt";
	public static final String FILENAME_PREVIOUS_RUNS_3WAY = "previousRunFile_3w.txt";


	public static final String PREVIOUS_RUN_PATH = "PREVIOUS_RUN_PATH";
	public static final String CHAT_ID = "CHAT_ID";
	public static final int STORED_RUNS_MAX = 40;
	public static final String IMAGE_ID = "IMAGEID";
	public static final String ARBS_PB_MIN_THRESHOLD = "ARBS_BETFAIR_MIN_THRESHOLD";
	public static final String ARBS_PB_MAX_THRESHOLD = "ARBS_BETFAIR_MAX_THRESHOLD";
	public static final String ARBS_PP_MIN_THRESHOLD = "ARBS_TXODDS_MIN_THRESHOLD";
	public static final String ARBS_PP_MAX_THRESHOLD = "ARBS_TXODDS_MAX_THRESHOLD";
	public static final String MINUTES_ODD_VALIDITY = "MINUTES_ODD_VALIDITY";
	public static final String ARBS_SIZE_MIN_VALUE = "ARBS_SIZE_MIN_VALUE";
	public static final String FREQ_ARBS_SEC = "FREQ_ARBS_SEC";
	public static final String XHTML_PATH = "XHTML_PATH";
	public static final String SPAM_THRESHOLD_COUNT = "SPAM_THRESHOLD_COUNT";
	public static final String BETTER_ODD_PERCENTAGE = "BETTER_ODD_PERCENTAGE";
	public static final String THREEWAY_NET_PROFIT = "THREEWAY_NET_PROFIT";
	public static final String MARATHON_BET_BOOKMAKER_NAME = "Marathonbet";
		
	
	// SOGLIA OLTRE LA QUALE SI MOSTRANO I MISSILI NELLA NOTIFICA TELEGRAM
	public static final String MISSILE_TRASHOLD = "MISSILE_TRASHOLD";

	// TELEGRAM BOT
	public static final String TELEGRAMBOTKEY = "TELEGRAMBOTKEY";
	public static final String TELEGRAMBOTNAME = "TELEGRAMBOTNAME";
	
	// TELEGRAM TERMINAL USERS IDS FILE
	public static final String TERMINALUSERSFILE = "TERMINALUSERSFILE";

	//DATABASE
	public static final String DATABASE_USER = "java.application";
	public static final String DATABASE_PASSWORD = "bluesheep2018";
//	//DATABASE
//	public static final String DATABASE_USER = "root";
//	public static final String DATABASE_PASSWORD = "Desaparecido44";

	//CHAT_BOT
	public static final String CHAT_BOT_MAX_PREF = "CHAT_BOT_MAX_PREF";
	public static final String TELEGRAM_USER_CHATID_REGEX = "CHAT_ID=";
	public static final String REGEX_TELEGRAMBOTACTION = " - ";
	public static final String WS_USERS_PWD = "blu3S433p-w5-|?o";
	public static final String MAX_NOTIFICATION_USER_PREF = "MAX_NOTIFICATION_USER_PREF";
	public static final String ADMITTED_CHATID = "ADMITTED_CHATID";
	
	private BlueSheepConstants() {}
}
