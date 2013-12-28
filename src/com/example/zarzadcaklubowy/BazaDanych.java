package com.example.zarzadcaklubowy;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BazaDanych {
	private static final String DEBUG_TAG = "Baza Danych";
	 
    private static final int DB_VERSION = 41;
    private static final String DB_NAME = "database.db";
    
    
    private static final String DB_DANE_KLUBU_TABELA= "daneklubu";
    private static final String DB_SEZON_TABELA = "sezon";
    private static final String DB_KOLEJKA_TABELA = "kolejka";
    private static final String DB_DRUZYNA_TABELA = "druzyna";
    private static final String DB_ZAWODNIK_TABELA = "zawodnik";
    private static final String DB_MECZ_TABELA = "mecz";
    private static final String DB_STATYSTYKA_TABELA = "statystyka";
    private static final String DB_ZMIANA_TABELA = "zmiana";
    private static final String DB_WYKLUCZENIE_TABELA = "wykluczenie";
    private static final String DB_DZIALACZE_TABELA = "dzialacze";
    
    
    public static final String PK_AUTO = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String TX_NOT_NULL = "TEXT NOT NULL";
    public static final String INT = "INTEGER";
    public static final String TEXT = "TEXT";
    public static final String INT_NOT_NULL = "INTEGER NOT NULL";
    public static final String FLOAT_NOT_NULL = "FLOAT NOT NULL";
    public static final String FLOAT = "FLOAT";
    
    
    private static final String DB_CREATE_DANE_KLUBU_TABLE =
            "CREATE TABLE " + DB_DANE_KLUBU_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", dan_sez_id" + " " + INT_NOT_NULL +
            ", dan_dru_id" + " " + INT_NOT_NULL + 
            ", dan_rok" + " " + INT + 
            ", dan_adres " + TEXT + 
            ", dan_telefon " + INT +
            ");";
    
    private static final String DB_CREATE_SEZON_TABLE =
            "CREATE TABLE " + DB_SEZON_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", sez_numer" + " " + TX_NOT_NULL + 
            ", sez_liczbakolejek" + " " + INT_NOT_NULL +
            ", sez_aktualny" + " " + INT_NOT_NULL + " DEFAULT 0" +
            ");";
    
    private static final String DB_CREATE_KOLEJKA_TABLE =
            "CREATE TABLE " + DB_KOLEJKA_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", kol_sez_id" + " " + INT_NOT_NULL +
            ", kol_numer " + INT_NOT_NULL +
            ");";
    
    private static final String DB_CREATE_DRUZYNA_TABLE =
            "CREATE TABLE " + DB_DRUZYNA_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", dru_sez_id" + " " + INT_NOT_NULL +
            ", dru_nazwa" + " " + TX_NOT_NULL + 
            ", dru_mecze " + INT_NOT_NULL + " DEFAULT 0" +
            ", dru_punkty " + INT_NOT_NULL + " DEFAULT 0" +
            ", dru_zwyciestwa " + INT_NOT_NULL + " DEFAULT 0" +
            ", dru_remisy " + INT_NOT_NULL + " DEFAULT 0" +
            ", dru_porazki " + INT_NOT_NULL + " DEFAULT 0" +
            ", dru_bramki_zdobyte " + INT_NOT_NULL + " DEFAULT 0" +
            ", dru_bramki_stracone " + INT_NOT_NULL + " DEFAULT 0" +
            ");";
    
    private static final String DB_CREATE_ZAWODNIK_TABLE =
            "CREATE TABLE " + DB_ZAWODNIK_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", zaw_sez_id" + " " + INT_NOT_NULL +
            ", zaw_dru_id" + " " + INT_NOT_NULL +
            ", zaw_imie" + " " + TX_NOT_NULL + 
            ", zaw_nazwisko" + " " + TX_NOT_NULL + 
            ", zaw_PESEL " + TX_NOT_NULL +
            ", zaw_adres " + TX_NOT_NULL +
            ", zaw_data " + TX_NOT_NULL +
            ", zaw_nrtel " + TX_NOT_NULL + " DEFAULT '---'" +
            ", zaw_email " + TEXT + " DEFAULT '---'" +
            ", zaw_pozycja " + TEXT + " DEFAULT '---'" +
            ", zaw_mecze " + INT_NOT_NULL + " DEFAULT 0" +
            ", zaw_minuty " + INT_NOT_NULL + " DEFAULT 0" +
            ", zaw_zolte_kartki " + INT_NOT_NULL + " DEFAULT 0" +
            ", zaw_czerw_kartki " + INT_NOT_NULL + " DEFAULT 0" +
            ", zaw_bramki " + INT_NOT_NULL + " DEFAULT 0" +
            ");";
    
    private static final String DB_CREATE_MECZ_TABLE =
            "CREATE TABLE " + DB_MECZ_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", mecz_sez_id" + " " + INT_NOT_NULL +  
            ", mecz_dru_id_gospodarz " + INT_NOT_NULL + 
            ", mecz_dru_id_gosc " + INT_NOT_NULL +
            ", mecz_kolejka" + " " + INT_NOT_NULL +
            ", mecz_bramkigosp " +  TEXT + " DEFAULT ''"+
            ", mecz_bramkigosc " + TEXT + " DEFAULT ''"+
            ", mecz_data " + "DATE" +
            ", mecz_godzina " + "TIME" + 
            ");";
    
    private static final String DB_CREATE_STATYSTYKA_TABLE =
            "CREATE TABLE " + DB_STATYSTYKA_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", sta_mecz_id" + " " + INT_NOT_NULL + 
            ", sta_zaw_id" + " " + INT_NOT_NULL +
            ", sta_nazwa " + TX_NOT_NULL + 
            ", sta_minuta " + INT_NOT_NULL +
            ");";
    
    private static final String DB_CREATE_ZMIANA_TABLE =
            "CREATE TABLE " + DB_ZMIANA_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", zmi_zaw1_id" + " " + INT_NOT_NULL +
            ", zmi_zaw2_id" + " " + INT_NOT_NULL +
            ", zmi_mecz_id" + " " + INT_NOT_NULL +
            ", zmi_minuta " + INT_NOT_NULL +
            ");";
    
    private static final String DB_CREATE_WYKLUCZENIE_TABLE =
            "CREATE TABLE " + DB_WYKLUCZENIE_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", wyk_sez_id" + " " + INT_NOT_NULL +
            ", wyk_mecz_id"+ " " + INT_NOT_NULL +
            ", wyk_zaw_id" + " " + INT_NOT_NULL +
            ", wyk_aktywne" + " " + INT_NOT_NULL +
            ", wyk_il_meczow" + " " + INT_NOT_NULL +
            ", wyk_po_ilu " + INT_NOT_NULL +
            ");";
    
    
    private static final String DB_CREATE_DZIALACZE_TABLE =
            "CREATE TABLE " + DB_DZIALACZE_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", dzi_sez_id" + " " + INT_NOT_NULL +
            ", dzi_dru_id" + " " + INT_NOT_NULL +
            ", dzi_imie" + " " + TX_NOT_NULL + 
            ", dzi_nazwisko" + " " + TX_NOT_NULL + 
            ", dzi_PESEL " + TX_NOT_NULL +
            ", dzi_adres " + TX_NOT_NULL +
            ", dzi_data " + TX_NOT_NULL +
            ", dzi_nrtel " + TX_NOT_NULL + " DEFAULT '---'" +
            ", dzi_email " + TEXT + " DEFAULT '---'" +
            ", dzi_stanowisko " + TEXT + " DEFAULT '---'" +
            ");";
    
    private static final String DROP_DANE_KLUBU =
            "DROP TABLE IF EXISTS " + DB_DANE_KLUBU_TABELA;
    private static final String DROP_SEZON =
            "DROP TABLE IF EXISTS " + DB_SEZON_TABELA;
    private static final String DROP_KOLEJKA=
            "DROP TABLE IF EXISTS " + DB_KOLEJKA_TABELA;
    private static final String DROP_DRUZYNA=
            "DROP TABLE IF EXISTS " + DB_DRUZYNA_TABELA;
    private static final String DROP_ZAWODNIK=
            "DROP TABLE IF EXISTS " + DB_ZAWODNIK_TABELA;
    private static final String DROP_MECZ=
            "DROP TABLE IF EXISTS " + DB_MECZ_TABELA;
    private static final String DROP_STATYSTYKA=
            "DROP TABLE IF EXISTS " + DB_STATYSTYKA_TABELA;
    private static final String DROP_ZMIANA=
            "DROP TABLE IF EXISTS " + DB_ZMIANA_TABELA;
    private static final String DROP_WYKLUCZENIE=
            "DROP TABLE IF EXISTS " + DB_WYKLUCZENIE_TABELA;
    private static final String DROP_DZIALACZE=
            "DROP TABLE IF EXISTS " + DB_DZIALACZE_TABELA;
    
    private SQLiteDatabase db;
    private Context context;
    private DatabaseHelper dbHelper;
    
    
    public SQLiteDatabase getDB()
    {
    	return db;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {  	
            super(context, name, factory, version);
        }
     
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE_DANE_KLUBU_TABLE);
            db.execSQL(DB_CREATE_SEZON_TABLE);
            db.execSQL(DB_CREATE_KOLEJKA_TABLE);
            db.execSQL(DB_CREATE_DRUZYNA_TABLE);
            db.execSQL(DB_CREATE_ZAWODNIK_TABLE);
            db.execSQL(DB_CREATE_MECZ_TABLE);
            db.execSQL(DB_CREATE_STATYSTYKA_TABLE);
            db.execSQL(DB_CREATE_ZMIANA_TABLE);
            db.execSQL(DB_CREATE_WYKLUCZENIE_TABLE);
            db.execSQL(DB_CREATE_DZIALACZE_TABLE);
     
            Log.d(DEBUG_TAG, "Tworzenie bazy...");
            Log.d(DEBUG_TAG, "Tabela " + DB_DANE_KLUBU_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_SEZON_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_KOLEJKA_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_DRUZYNA_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_ZAWODNIK_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_MECZ_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_STATYSTYKA_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_ZMIANA_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_WYKLUCZENIE_TABELA + " ver." + DB_VERSION + " utworzona");
            Log.d(DEBUG_TAG, "Tabela " + DB_DZIALACZE_TABELA + " ver." + DB_VERSION + " utworzona");
        }
     
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(DROP_DANE_KLUBU);
            db.execSQL(DROP_SEZON);
            db.execSQL(DROP_KOLEJKA);
            db.execSQL(DROP_DRUZYNA);
            db.execSQL(DROP_ZAWODNIK);
            db.execSQL(DROP_MECZ);
            db.execSQL(DROP_STATYSTYKA);
            db.execSQL(DROP_ZMIANA);
            db.execSQL(DROP_WYKLUCZENIE);
            db.execSQL(DROP_DZIALACZE);
     
            Log.d(DEBUG_TAG, "Baza danych aktualizowana...");
            Log.d(DEBUG_TAG, "Tabela " + DB_DANE_KLUBU_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_SEZON_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_KOLEJKA_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_DRUZYNA_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_ZAWODNIK_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_MECZ_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_STATYSTYKA_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_ZMIANA_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            Log.d(DEBUG_TAG, "Tabela " + DB_WYKLUCZENIE_TABELA + " zaktualizowana z ver." + oldVersion + " do ver." + newVersion);
            
            Log.d(DEBUG_TAG, "Wszystkie dane wyczyszczone.");
     
            onCreate(db);
        }
    }
    
    public BazaDanych(Context context) {
        this.context = context;
    }
    
    public BazaDanych open(){
        dbHelper = new DatabaseHelper(context, DB_NAME, null, DB_VERSION);
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLException e) {
            db = dbHelper.getReadableDatabase();
        }
        return this;
    }
    
    public void close() {
        dbHelper.close();
    }
    
    
    
    
    /* --------------------------------DANE_KLUBU----------------------------------------*/  
    
    
    public long insertDaneKlubu(long sezon_id, long druzyna_id, int rok, String adres, int telefon) {
        ContentValues newDaneKlubu = new ContentValues();
        newDaneKlubu.put("dan_sez_id", sezon_id);
        newDaneKlubu.put("dan_dru_id", druzyna_id);
        newDaneKlubu.put("dan_rok", rok);
        newDaneKlubu.put("dan_adres", adres);
        newDaneKlubu.put("dan_telefon", telefon);
        return db.insert(DB_DANE_KLUBU_TABELA, null, newDaneKlubu);
    }
    
    public boolean updateDaneKlubu(long id, int rok, String adres, int telefon) {
        String where = "_id" + "=" + id;
        ContentValues newDaneKlubu = new ContentValues();
        newDaneKlubu.put("_id", id);
        newDaneKlubu.put("dan_rok", rok);
        newDaneKlubu.put("dan_adres", adres);
        newDaneKlubu.put("dan_telefon", telefon);
        return db.update(DB_DANE_KLUBU_TABELA, newDaneKlubu, where, null) > 0;
    }
    
    public boolean deleteDaneKlubu(long id){
        String where = "_id" + "=" + id;
        return db.delete(DB_DANE_KLUBU_TABELA, where, null) > 0;
    }
    
    public boolean deleteDaneKlubu(){
        return db.delete(DB_DANE_KLUBU_TABELA, null, null) > 0;
    }
    
    public boolean deleteDaneAktualnegoSezonu(long id){
    	
    	String where = "dan_sez_id" + "=" + id;
        return db.delete(DB_DANE_KLUBU_TABELA, where, null) > 0;
        
    }
    
    
    public Cursor getDaneKlubu() {
        //String[] columns = {"_id", "dan_sez_id","dan_dru_id","dan_rok","dan_adres","dan_telefon"};
        //return db.query(DB_DANE_KLUBU_TABELA, columns, null, null, null, null, null);
        return db.rawQuery("select daneklubu._id,dan_sez_id,dan_dru_id,dan_rok,dan_adres,dan_telefon " +
        		"from daneklubu,sezon where daneklubu.dan_sez_id=sezon._id and sezon.sez_aktualny=1",null);
    }
   
   
    
    
    
    /* -------------------------------DRUZYNA----------------------------------------*/  
    
    public long insertDruzyna(long sezon, String nazwa) {
        ContentValues newDruzyna = new ContentValues();
        newDruzyna.put("dru_sez_id", sezon);
        newDruzyna.put("dru_nazwa", nazwa);
        return db.insert(DB_DRUZYNA_TABELA, null, newDruzyna);
    }
    
    public Cursor getNazwyDruzyn() {
        return db.rawQuery("select druzyna._id,dru_nazwa,dru_mecze,dru_punkty,dru_zwyciestwa,dru_remisy,dru_porazki,dru_bramki_zdobyte,dru_bramki_stracone" +
        		" from druzyna,sezon where druzyna.dru_sez_id=sezon._id and sezon.sez_aktualny=1", null);
    }
    
    public Cursor getDruzyny() {
        String[] columns = {"_id","dru_nazwa","dru_mecze","dru_punkty",
        		"dru_zwyciestwa","dru_remisy","dru_porazki","dru_bramki_zdobyte","dru_bramki_stracone"};
        return db.query(DB_DRUZYNA_TABELA, columns, null, null, null, null, null);
    }
    
    public Cursor getNazwyDruzyn(long id) {
    	String where = "_id" + "=" + id;
        String[] columns = {"_id","dru_nazwa","dru_mecze","dru_punkty",
        		"dru_zwyciestwa","dru_remisy","dru_porazki","dru_bramki_zdobyte","dru_bramki_stracone"};
        return db.query(DB_DRUZYNA_TABELA, columns, where, null, null, null, null);
    }
    
    public boolean updateDruzyna(long id, int mecze, int punkty, int zwyciestwa,int remisy, int porazki,int brZdobyte, int brStracone ) {
        
    	String where = "_id" + "=" + id;
    	ContentValues newDruzyna = new ContentValues();
        newDruzyna.put("dru_mecze", mecze); 
        newDruzyna.put("dru_punkty", punkty);
        newDruzyna.put("dru_zwyciestwa", zwyciestwa);
        newDruzyna.put("dru_remisy", remisy);
        newDruzyna.put("dru_porazki", porazki);
        newDruzyna.put("dru_bramki_zdobyte", brZdobyte);
        newDruzyna.put("dru_bramki_stracone", brStracone);
        return db.update(DB_DRUZYNA_TABELA, newDruzyna, where, null) > 0;
    }
    
public boolean updateNazwaDruzyny(long id, String nazwa) {
        
    	String where = "_id" + "=" + id;
    	ContentValues newDruzyna = new ContentValues();
        newDruzyna.put("dru_nazwa", nazwa); 
        return db.update(DB_DRUZYNA_TABELA, newDruzyna, where, null) > 0;
    }
    
    public boolean deleteDruzyny(){
        return db.delete(DB_DRUZYNA_TABELA, null, null) > 0;
        
    }
    
    public boolean deleteDruzyny(long id){
    	
    	String where = "_id" + "=" + id;
        return db.delete(DB_DRUZYNA_TABELA, where, null) > 0;
        
    }
    
    public boolean deleteDruzynyAktualnegoSezonu(long id){
    	
    	String where = "dru_sez_id" + "=" + id;
        return db.delete(DB_DRUZYNA_TABELA, where, null) > 0;
        
    }
    
    public Cursor getDruzynyDoTabeli() {
        return db.rawQuery("select druzyna._id,dru_nazwa,dru_mecze,dru_punkty,dru_zwyciestwa,dru_remisy,dru_porazki,dru_bramki_zdobyte,dru_bramki_stracone " +
    "from druzyna,sezon where druzyna.dru_sez_id=sezon._id and sezon.sez_aktualny=1 order by dru_punkty desc,dru_bramki_zdobyte-dru_bramki_stracone desc", null);
    }
    
    /* --------------------------------ZAWODNICY----------------------------------------*/  
    
    public long insertZawodnik(long sezon_id, long druzyna_id, String imie, String nazwisko, String pesel, String adres, String data, String nrtel, String email, String pozycja) {
        ContentValues newDruzyna = new ContentValues();
        newDruzyna.put("zaw_sez_id", sezon_id);
        newDruzyna.put("zaw_dru_id", druzyna_id);
        newDruzyna.put("zaw_imie", imie);
        newDruzyna.put("zaw_nazwisko", nazwisko);
        newDruzyna.put("zaw_PESEL", pesel);
        newDruzyna.put("zaw_adres", adres);
        newDruzyna.put("zaw_data", data);
        newDruzyna.put("zaw_nrtel", nrtel);
        newDruzyna.put("zaw_email", email);
        newDruzyna.put("zaw_pozycja", pozycja); 
        return db.insert(DB_ZAWODNIK_TABELA, null, newDruzyna);
    }
    
    public Cursor getZawodnicy() {
        /*String[] columns = {"_id","zaw_dru_id","zaw_imie","zaw_nazwisko","zaw_PESEL","zaw_adres","zaw_data","zaw_nrtel",
        		"zaw_email","zaw_pozycja","zaw_mecze","zaw_minuty","zaw_zolte_kartki","zaw_czerw_kartki","zaw_bramki "};
        return db.query(DB_ZAWODNIK_TABELA, columns, null, null, null, null, null);*/
    	return db.rawQuery("select zawodnik._id,zaw_sez_id,zaw_dru_id,zaw_imie,zaw_nazwisko,zaw_PESEL,zaw_adres,zaw_data,zaw_nrtel,zaw_email,zaw_pozycja,zaw_mecze,zaw_minuty,zaw_zolte_kartki,zaw_czerw_kartki,zaw_bramki"+
    			" from zawodnik,sezon where zawodnik.zaw_sez_id=sezon._id and sezon.sez_aktualny=1", null);

    }
    
    public Cursor getZawodnicy(long id) {
    	String where = "_id" + "=" + id;
        String[] columns = {"_id","zaw_dru_id","zaw_imie","zaw_nazwisko","zaw_PESEL","zaw_adres","zaw_data","zaw_nrtel",
        		"zaw_email","zaw_pozycja","zaw_mecze","zaw_minuty","zaw_zolte_kartki","zaw_czerw_kartki","zaw_bramki "};
        return db.query(DB_ZAWODNIK_TABELA, columns, where, null, null, null, null);
       
        
    }
    
	 public Cursor getZawodnicyMecz(long meczID)
	 {

		 return db.rawQuery("select zawodnik._id,zaw_sez_id,zaw_dru_id,zaw_imie,zaw_nazwisko,zaw_PESEL,zaw_adres,zaw_data,zaw_nrtel,zaw_email,zaw_pozycja,zaw_mecze,zaw_minuty,zaw_zolte_kartki,zaw_czerw_kartki,zaw_bramki " +
		 "from zawodnik,statystyka where zawodnik._id=statystyka.sta_zaw_id and (statystyka.sta_nazwa='podstawowy' or statystyka.sta_nazwa='rezerwowy') and statystyka.sta_mecz_id = " + meczID,null);
	 }
	 
	 public Cursor getZawodnicyPodstawowi(long meczID)
	 {

		 return db.rawQuery("select zawodnik._id,zaw_sez_id,zaw_dru_id,zaw_imie,zaw_nazwisko,zaw_PESEL,zaw_adres,zaw_data,zaw_nrtel,zaw_email,zaw_pozycja,zaw_mecze,zaw_minuty,zaw_zolte_kartki,zaw_czerw_kartki,zaw_bramki " +
		 "from zawodnik,statystyka where zawodnik._id=statystyka.sta_zaw_id and statystyka.sta_nazwa='wejscie' and statystyka.sta_mecz_id = " + meczID + " except "+
		 "select zawodnik._id,zaw_sez_id,zaw_dru_id,zaw_imie,zaw_nazwisko,zaw_PESEL,zaw_adres,zaw_data,zaw_nrtel,zaw_email,zaw_pozycja,zaw_mecze,zaw_minuty,zaw_zolte_kartki,zaw_czerw_kartki,zaw_bramki " +
		 "from zawodnik,statystyka where zawodnik._id=statystyka.sta_zaw_id and statystyka.sta_nazwa='zejscie' and statystyka.sta_mecz_id = " + meczID,null);
	 }
    
    public boolean deleteZawodnik(long id){
    	
    	String where = "_id" + "=" + id;
        return db.delete(DB_ZAWODNIK_TABELA, where, null) > 0;
        
    }
    
    public boolean deleteZawodnikSezon(long id){
    	
    	String where = "zaw_sez_id" + "=" + id;
        return db.delete(DB_ZAWODNIK_TABELA, where, null) > 0;
        
    }

    
    public boolean updateZawodnikStatystyki(long id, int mecze, int minuty, int zKartki, int czKartki, int bramki) {
        
    	String where = "_id" + "=" + id;
    	ContentValues newZawodnik = new ContentValues();
    	newZawodnik.put("zaw_mecze", mecze); 
    	newZawodnik.put("zaw_minuty", minuty); 
    	newZawodnik.put("zaw_zolte_kartki", zKartki); 
    	newZawodnik.put("zaw_czerw_kartki", czKartki); 
    	newZawodnik.put("zaw_bramki", bramki); 
        return db.update(DB_ZAWODNIK_TABELA, newZawodnik, where, null) > 0;
    }
    
    public boolean updateZawodnik(long id,long druzyna_id, String imie, String nazwisko, String pesel, String adres, String data, String nrtel, String email, String pozycja) {
        
    	String where = "_id" + "=" + id;
    	ContentValues zawodnik = new ContentValues();
    	zawodnik.put("zaw_dru_id", druzyna_id);
    	zawodnik.put("zaw_imie", imie);
    	zawodnik.put("zaw_nazwisko", nazwisko);
    	zawodnik.put("zaw_PESEL", pesel);
    	zawodnik.put("zaw_adres", adres);
    	zawodnik.put("zaw_data", data);
    	zawodnik.put("zaw_nrtel", nrtel);
    	zawodnik.put("zaw_email", email);
    	zawodnik.put("zaw_pozycja", pozycja); 
        return db.update(DB_ZAWODNIK_TABELA, zawodnik, where, null) > 0;
    }
    
    
    /* --------------------------------SEZON----------------------------------------*/  
    
    
    public long insertSezon(String numer, int lkolejek, int ak)
    {
    	ContentValues newSezon = new ContentValues();
        newSezon.put("sez_numer", numer);
        newSezon.put("sez_liczbakolejek", lkolejek); 
        newSezon.put("sez_aktualny", ak);
        return db.insert(DB_SEZON_TABELA, null, newSezon);
    	
    }
    
    public Cursor getSezonAktualny() {
    	String where = "sez_aktualny" + "=1";
        String[] columns = {"_id","sez_numer","sez_liczbakolejek","sez_aktualny"};
        return db.query(DB_SEZON_TABELA, columns, where, null, null, null, null);

    }
    public Cursor getSezony() {
        String[] columns = {"_id","sez_numer","sez_liczbakolejek","sez_aktualny"};
        return db.query(DB_SEZON_TABELA, columns, null, null, null, null, null);

    }
    
    public boolean deleteSezony(){
        return db.delete(DB_SEZON_TABELA, null, null) > 0;
        
    }
    public boolean deleteSezonAktualny(){
    	String where = "sez_aktualny" + "=1";
        return db.delete(DB_SEZON_TABELA, where, null) > 0;
        
    }
    
    public boolean updateSezonyNieaktualne() {
        
    	String where = "sez_aktualny" + "=1";
    	ContentValues newSezon = new ContentValues();
        newSezon.put("sez_aktualny", 0);
        return db.update(DB_SEZON_TABELA, newSezon, where, null) > 0;
    }
    
	public boolean updateSezonAktualny(long id) {
			String where = "_id=" + id;
	    	ContentValues newSezon = new ContentValues();
	        newSezon.put("sez_aktualny", 1);
	        return db.update(DB_SEZON_TABELA, newSezon, where, null) > 0;
	    }
	
	public boolean updateSezon(String numer,int lkol) {
	    
		String where = "sez_aktualny" + "=1";
		ContentValues newSezon = new ContentValues();
	    newSezon.put("sez_numer", numer);
	    newSezon.put("sez_liczbakolejek", lkol); 
	    return db.update(DB_SEZON_TABELA, newSezon, where, null) > 0;
	}
	
	/* -------------------------------MECZ----------------------------------------*/ 
    
	
	public Cursor getMecze() {
        return db.rawQuery("select mecz._id,mecz_dru_id_gospodarz,mecz_dru_id_gosc,mecz_kolejka,mecz_bramkigosp," +
        		"mecz_bramkigosc,mecz_data,mecz_godzina" +
        		" from mecz,sezon where mecz.mecz_sez_id=sezon._id and sezon.sez_aktualny=1 order by mecz_kolejka", null);
    }
	
	public Cursor getMecze(int kol,long druzyna) {
		String k = String.valueOf(kol);
		String d = String.valueOf(druzyna);
        return db.rawQuery("select mecz._id,mecz_dru_id_gospodarz,mecz_dru_id_gosc,mecz_kolejka,mecz_bramkigosp," +
        		"mecz_bramkigosc,mecz_data,mecz_godzina" +
        		" from mecz,sezon where mecz.mecz_sez_id=sezon._id and sezon.sez_aktualny=1 and mecz_bramkigosp='' and mecz_bramkigosc='' and mecz_kolejka=" + k +
        		" and mecz_dru_id_gosc!=" + d + " and mecz_dru_id_gospodarz!=" + d, null);
    }
	
	
	
	public Cursor getMeczZDruzyna(long druzyna) {
		String d = String.valueOf(druzyna);
        return db.rawQuery("select * from mecz where mecz_dru_id_gospodarz=" + d + " or mecz_dru_id_gosc=" + d + " order by mecz_data,mecz_godzina", null);
    }
	
	public Cursor getMeczeZDruzynaPozniejsze(long druzyna, String data,long id) {
		String d = String.valueOf(druzyna);
        return db.rawQuery("select * from mecz where (mecz_dru_id_gospodarz=" + d + " or mecz_dru_id_gosc=" + d + ") and mecz_data>'" + data + "' and _id!=" + id, null);
    }
	
	public Cursor getMecz(long id) {
        return db.rawQuery("select * from mecz where _id="+id, null);
    }
	
	 public long insertMecz(long sez_id, long gosp_id,long gosc_id, int kolejka, String data, String godzina)
	    {
	    	ContentValues newMecz = new ContentValues();
	        newMecz.put("mecz_sez_id", sez_id);
	        newMecz.put("mecz_dru_id_gospodarz", gosp_id);
	        newMecz.put("mecz_dru_id_gosc", gosc_id);
	        newMecz.put("mecz_kolejka", kolejka);
	        //newMecz.put("mecz_bramkigosp", bramkigosp);
	        //newMecz.put("mecz_bramkigosc", bramkigosc);
	        newMecz.put("mecz_data", data);
	        newMecz.put("mecz_godzina", godzina);
	        return db.insert(DB_MECZ_TABELA, null, newMecz);
	    	
	    }
	 
	 public boolean deleteMeczeAktualnegoSezonu(long id){
	    	
	    	String where = "mecz_sez_id" + "=" + id;
	        return db.delete(DB_MECZ_TABELA, where, null) > 0;
	        
	    }
	 
	 public boolean deleteMecz(long id){
	    	
	    	String where = "_id" + "=" + id;
	        return db.delete(DB_MECZ_TABELA, where, null) > 0;
	        
	    }
	 
	 public boolean updateMecz(long id,String brgosp, String brgosc) {
		    
			String where = "_id=" + id;
			ContentValues newMecz = new ContentValues();
		    newMecz.put("mecz_bramkigosp", brgosp);
		    newMecz.put("mecz_bramkigosc", brgosc); 
		    return db.update(DB_MECZ_TABELA, newMecz, where, null) > 0;
		}
	
/*	private static final String DB_CREATE_MECZ_TABLE =
            "CREATE TABLE " + DB_MECZ_TABELA + "( " +
            "_id" + " " + PK_AUTO  +
            ", mecz_sez_id" + " " + INT_NOT_NULL +  
            ", mecz_dru_id_gospodarz " + INT_NOT_NULL + 
            ", mecz_dru_id_gosc " + INT_NOT_NULL +
            ", mecz_kolejka" + " " + INT_NOT_NULL +
            ", mecz_bramkigosp " + INT_NOT_NULL + " DEFAULT -1" +
            ", mecz_bramkigosc " + INT_NOT_NULL + " DEFAULT -1" +
            ", mecz_data " + "DATE" +
            ", mecz_godzina " + "TIME" + 
            ");";*/

	 
		/* -------------------------------STATYSTYKA----------------------------------------*/ 
	 
	/* private static final String DB_CREATE_STATYSTYKA_TABLE =
	            "CREATE TABLE " + DB_STATYSTYKA_TABELA + "( " +
	            "_id" + " " + PK_AUTO  +
	            ", sta_mecz_id" + " " + INT_NOT_NULL + 
	            ", sta_zaw_id" + " " + INT_NOT_NULL +
	            ", sta_nazwa " + TX_NOT_NULL + 
	            ", sta_minuta " + TX_NOT_NULL +
	            ");";*/
	 
	 public long insertStatystyka(long meczId, long zawodnikId,String nazwa, int minuta)
	 {
		 ContentValues newStatystyka = new ContentValues();
		 newStatystyka.put("sta_mecz_id", meczId);
		 newStatystyka.put("sta_zaw_id", zawodnikId);
		 newStatystyka.put("sta_nazwa", nazwa);
		 newStatystyka.put("sta_minuta", minuta);
		 return db.insert(DB_STATYSTYKA_TABELA, null, newStatystyka);
	 }
	 
	 public Cursor getStatystykiMeczNazwa(long meczID, String nazwa) {
	    	String where = "sta_mecz_id=" + meczID + " and sta_nazwa=" + "'" + nazwa +"'";
	        String[] columns = {"_id","sta_mecz_id","sta_zaw_id","sta_nazwa","sta_minuta"};
	        return db.query(DB_STATYSTYKA_TABELA, columns, where, null, null, null, null);

	    }
	 
	 public Cursor getStatystykiMeczNazwaZawodnik(long meczID, String nazwa, long zawodnikID) {
	    	String where = "sta_mecz_id=" + meczID + " and sta_nazwa=" + "'" + nazwa +"'" + " and sta_zaw_id=" + zawodnikID;
	        String[] columns = {"_id","sta_mecz_id","sta_zaw_id","sta_nazwa","sta_minuta"};
	        return db.query(DB_STATYSTYKA_TABELA, columns, where, null, null, null, null);

	    }
	 
	 public Cursor getStatystykiMeczNazwaMinuta(long meczID, String nazwa, int minuta) {
	    	String where = "sta_mecz_id=" + meczID + " and sta_nazwa=" + "'" + nazwa +"'" + " and sta_minuta=" + minuta;
	        String[] columns = {"_id","sta_mecz_id","sta_zaw_id","sta_nazwa","sta_minuta"};
	        return db.query(DB_STATYSTYKA_TABELA, columns, where, null, null, null, null);

	    }
	 
	 public Cursor getStatystykiMeczZawodnik(long meczID, long zawodnikID) {
	    	String where = "sta_mecz_id=" + meczID + " and sta_zaw_id=" + zawodnikID;
	        String[] columns = {"_id","sta_mecz_id","sta_zaw_id","sta_nazwa","sta_minuta"};
	        return db.query(DB_STATYSTYKA_TABELA, columns, where, null, null, null, null);

	    }
	 
	 public Cursor getStatystykiMeczu(long meczID) {
	        return db.rawQuery("select * from statystyka where sta_mecz_id=" + meczID + " order by sta_minuta desc",null);

	    }
	 
	 public Cursor getStatystykiBezSkladu(long meczID) {
	        return db.rawQuery("select * from statystyka where sta_mecz_id=" + 
	        meczID + " and (sta_nazwa='bramka' or sta_nazwa='zolta' or sta_nazwa='czerwona' or sta_nazwa='koniec'" +
	        		"  or sta_nazwa='zmianazejscie'  or sta_nazwa='zmianawejscie' or sta_nazwa='bramkaprzeciwnik')" +
	        " order by sta_minuta asc",null);

	    }
	 
	 public boolean deleteStatystykaMecz(long meczID){
	    	
	    	String where = "sta_mecz_id" + "=" + meczID;
	        return db.delete(DB_STATYSTYKA_TABELA, where, null) > 0;

	        	        
	    }
	 
	 public boolean deleteStatystyka(long id){
	    	
	    	String where = "_id" + "=" + id;
	        return db.delete(DB_STATYSTYKA_TABELA, where, null) > 0;

	        	        
	    }

	 

	 
	 public boolean deleteStatystyki()
	 {
		 return db.delete(DB_STATYSTYKA_TABELA,null, null) > 0;
	 }
	 
	 
	 
	 /*---------------------------WYKLUCZENIE_TABELA-------------------------------*/
	 
   /*  "_id" + " " + PK_AUTO  +
     ", wyk_sez_id" + " " + INT_NOT_NULL +
     ", wyk_mecz_id " + INT_NOT_NULL +
     ", wyk_zaw_id" + " " + INT_NOT_NULL +
     ", wyk_aktywne" + " " + INT_NOT_NULL +
     ", wyk_il_meczow" + " " + INT_NOT_NULL +
     ", wyk_po_ilu " + INT_NOT_NULL +
     ");";*/
	 
     public long insertWykluczenie(long sezonId,long meczId, long zawodnikId,int aktywne, int iloscMeczow, int poIlu)
	 {
		 ContentValues newWykluczenie = new ContentValues();
		 newWykluczenie.put("wyk_sez_id", sezonId);
		 newWykluczenie.put("wyk_mecz_id", meczId);
		 newWykluczenie.put("wyk_zaw_id", zawodnikId);
		 newWykluczenie.put("wyk_aktywne", aktywne);
		 newWykluczenie.put("wyk_il_meczow", iloscMeczow);
		 newWykluczenie.put("wyk_po_ilu", poIlu);
		 
		 return db.insert(DB_WYKLUCZENIE_TABELA, null, newWykluczenie);
	 }
     
     public boolean updateWykluczenie(long id, long sezonId,long meczId, long zawodnikId,int aktywne, int iloscMeczow, int poIlu) {
			String where = "_id=" + id;
			ContentValues newWykluczenie = new ContentValues();
			 newWykluczenie.put("wyk_sez_id", sezonId);
			 newWykluczenie.put("wyk_mecz_id", meczId);
			 newWykluczenie.put("wyk_zaw_id", zawodnikId);
			 newWykluczenie.put("wyk_aktywne", aktywne);
			 newWykluczenie.put("wyk_il_meczow", iloscMeczow);
			 newWykluczenie.put("wyk_po_ilu", poIlu);
	        return db.update(DB_WYKLUCZENIE_TABELA, newWykluczenie, where, null) > 0;
	    }
     
     public boolean updateWykluczenie(long id, int aktywne, int iloscMeczow) {
			String where = "_id=" + id;
			ContentValues newWykluczenie = new ContentValues();
			 newWykluczenie.put("wyk_aktywne", aktywne);
			 newWykluczenie.put("wyk_il_meczow", iloscMeczow);
	        return db.update(DB_WYKLUCZENIE_TABELA, newWykluczenie, where, null) > 0;
	    }
     
     
     
     
     public Cursor getWykluczenia() {
         return db.rawQuery("select wykluczenie._id,wyk_sez_id,wyk_mecz_id,wyk_zaw_id,wyk_aktywne,wyk_il_meczow,wyk_po_ilu " +
         		 "from wykluczenie,sezon where wykluczenie.wyk_sez_id=sezon._id and sezon.sez_aktualny=1", null);
     }
     
     public Cursor getWykluczeniaZawodnik(long zawodnikId) {
         return db.rawQuery("select wykluczenie._id,wyk_sez_id,wyk_mecz_id,wyk_zaw_id,wyk_aktywne,wyk_il_meczow,wyk_po_ilu " +
         		 "from wykluczenie,sezon where wykluczenie.wyk_sez_id=sezon._id and sezon.sez_aktualny=1 and wyk_zaw_id=" + zawodnikId, null);
     }
     
     public Cursor getWykluczeniaZawodnikMecz(long zawodnikId, long meczID) {
         return db.rawQuery("select wykluczenie._id,wyk_sez_id,wyk_mecz_id,wyk_zaw_id,wyk_aktywne,wyk_il_meczow,wyk_po_ilu " +
         		 "from wykluczenie,sezon where wykluczenie.wyk_sez_id=sezon._id and sezon.sez_aktualny=1 and wyk_zaw_id=" + zawodnikId + " and wyk_mecz_id=" + meczID, null);
     }
     
     public Cursor getWykluczeniaZawodnikPoIlu(long zawodnikId,int poilu) {
         return db.rawQuery("select wykluczenie._id,wyk_sez_id,wyk_mecz_id,wyk_zaw_id,wyk_aktywne,wyk_il_meczow,wyk_po_ilu " +
         		 "from wykluczenie,sezon where wykluczenie.wyk_sez_id=sezon._id and sezon.sez_aktualny=1 and wyk_zaw_id=" + zawodnikId + " and wyk_po_ilu="+ poilu, null);
     }
     
     public Cursor getWykluczeniaZawodnikAktywne(long zawodnikId) {
         return db.rawQuery("select wykluczenie._id,wyk_sez_id,wyk_mecz_id,wyk_zaw_id,wyk_aktywne,wyk_il_meczow,wyk_po_ilu " +
         		 "from wykluczenie,sezon where wykluczenie.wyk_sez_id=sezon._id and sezon.sez_aktualny=1 and wyk_aktywne=1 and wyk_zaw_id=" + zawodnikId, null);
     }
     
     public boolean deleteWykluczenia()
	 {
		 return db.delete(DB_WYKLUCZENIE_TABELA,null, null) > 0;
	 }
     
     public boolean deleteWykluczenie(long meczID)
	 {
    	 String where = "wyk_mecz_id=" + meczID;
		 return db.delete(DB_WYKLUCZENIE_TABELA,where, null) > 0;
	 }
     
     public boolean deleteWykluczeniePoID(long ID)
	 {
    	 String where = "_id=" + ID;
		 return db.delete(DB_WYKLUCZENIE_TABELA,where, null) > 0;
	 }
     
     
     
	 
     
     /*------------------------------DZIALACZE-------------------------------------*/
     
     public long insertDzialacz(long sezon_id, long druzyna_id, String imie, String nazwisko, String pesel, String adres, String data, String nrtel, String email, String stanowisko) {
         ContentValues newDzialacz = new ContentValues();
         newDzialacz.put("dzi_sez_id", sezon_id);
         newDzialacz.put("dzi_dru_id", druzyna_id);
         newDzialacz.put("dzi_imie", imie);
         newDzialacz.put("dzi_nazwisko", nazwisko);
         newDzialacz.put("dzi_PESEL", pesel);
         newDzialacz.put("dzi_adres", adres);
         newDzialacz.put("dzi_data", data);
         newDzialacz.put("dzi_nrtel", nrtel);
         newDzialacz.put("dzi_email", email);
         newDzialacz.put("dzi_stanowisko", stanowisko); 
         return db.insert(DB_DZIALACZE_TABELA, null, newDzialacz);
     }
     
     public Cursor getDzialacze() {
         /*String[] columns = {"_id","zaw_dru_id","zaw_imie","zaw_nazwisko","zaw_PESEL","zaw_adres","zaw_data","zaw_nrtel",
         		"zaw_email","zaw_pozycja","zaw_mecze","zaw_minuty","zaw_zolte_kartki","zaw_czerw_kartki","zaw_bramki "};
         return db.query(DB_ZAWODNIK_TABELA, columns, null, null, null, null, null);*/
     	return db.rawQuery("select dzialacze._id,dzi_sez_id,dzi_dru_id,dzi_imie,dzi_nazwisko,dzi_PESEL,dzi_adres,dzi_data,dzi_nrtel,dzi_email,dzi_stanowisko "+
     			" from dzialacze,sezon where dzialacze.dzi_sez_id=sezon._id and sezon.sez_aktualny=1", null);

     }
     
     public Cursor getDzialacze(long id) {
     	String where = "_id" + "=" + id;
         String[] columns = {"_id","dzi_dru_id","dzi_imie","dzi_nazwisko","dzi_PESEL","dzi_adres","dzi_data","dzi_nrtel",
         		"dzi_email","dzi_stanowisko "};
         return db.query(DB_DZIALACZE_TABELA, columns, where, null, null, null, null);
        
         
     }
     

     
     public boolean deleteDzialacz(long id){
     	
     	String where = "_id" + "=" + id;
         return db.delete(DB_DZIALACZE_TABELA, where, null) > 0;
         
     }
     
     public boolean deleteDzialaczSezon(long id){
     	
     	String where = "zaw_sez_id" + "=" + id;
         return db.delete(DB_DZIALACZE_TABELA, where, null) > 0;
         
     }

     
     public boolean updateDzialacz(long id,long druzyna_id, String imie, String nazwisko, String pesel, String adres, String data, String nrtel, String email, String stanowisko) {
         
     	String where = "_id" + "=" + id;
     	ContentValues dzialacz = new ContentValues();
     	dzialacz .put("dzi_dru_id", druzyna_id);
     	dzialacz .put("dzi_imie", imie);
     	dzialacz .put("dzi_nazwisko", nazwisko);
     	dzialacz .put("dzi_PESEL", pesel);
     	dzialacz .put("dzi_adres", adres);
     	dzialacz .put("dzi_data", data);
     	dzialacz .put("dzi_nrtel", nrtel);
     	dzialacz .put("dzi_email", email);
     	dzialacz .put("dzi_stanowisko", stanowisko); 
        return db.update(DB_DZIALACZE_TABELA, dzialacz , where, null) > 0;
     }
     
	 
	 
}
