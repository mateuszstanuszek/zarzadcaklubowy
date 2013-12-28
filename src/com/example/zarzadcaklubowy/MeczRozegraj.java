package com.example.zarzadcaklubowy;

import java.util.ArrayList;
import java.util.List;


import android.os.Bundle;
import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MeczRozegraj extends Activity implements OnClickListener {

	private ListView lvPodstawowi, lvRezerwowi;
	private Button btTyl, btPrzod, btZakoncz, btTyl5, btPrzod5, btStatystyki, btBramkaPrzeciwnik,btZmiana;
	private TextView tvGospodarz, tvGosc, tvBrGospodarz, tvBrGosc, tvMinuta;
	private Context context;
	private BazaDanych baza;
	private long meczID, gospodarzID, goscID;
	private Cursor /*cursorPodstawowi, cursorRezerwowi,*/ cursorSezon, cursorMecz, cursorGospodarz, cursorGosc, cursorMojaDruzyna, cursorStatystyki;
	private ArrayList<ZawodnikMecz> podstawowi,rezerwowi;
	private RozegrajArrayAdapter podstawowiAdapter, rezerwowiAdapter;
	private boolean CzyMoznaZakonczyc = false;
	private long mojaDruzynaID,sezonID;
	private ZawodnikMecz zmianaPodstawowy,zmianaRezerwowy;
	private int pozycjaPodstawowi = -2;
	private int pozycjaRezerwowi = -2;
	private ArrayList<Integer> wykluczeniaPoIlu;
	private Cursor cursorZmiany;
	private int iloscZmian;
	private int doliczoneMinuty;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mecz_rozegraj);
		PrzypiszId();
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		
		wykluczeniaPoIlu = new ArrayList<Integer>();
		wykluczeniaPoIlu.add(4);
		wykluczeniaPoIlu.add(8);
		wykluczeniaPoIlu.add(11);
		wykluczeniaPoIlu.add(13);
		wykluczeniaPoIlu.add(15);
		wykluczeniaPoIlu.add(17);
		
		
		WczytajDane();
		
		
		
		
	}
	
	
	private void WczytajDane()
	{
		cursorSezon = baza.getSezonAktualny();
		if(cursorSezon!=null && cursorSezon.moveToFirst())
		{
			sezonID = cursorSezon.getLong(cursorSezon.getColumnIndex("_id"));
		}
		podstawowi = new ArrayList<ZawodnikMecz>();
		rezerwowi = new ArrayList<ZawodnikMecz>();
		
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
			
		    meczID = extras.getLong("mecz");   
		    cursorZmiany = baza.getStatystykiMeczNazwa(meczID, "zmianazejscie");
		    if(cursorZmiany!=null && cursorZmiany.moveToFirst())
		    {
		    	iloscZmian = cursorZmiany.getCount();
		    }
		    Cursor cursorMinuta = baza.getStatystykiMeczu(meczID);
		    if(cursorMinuta!=null && cursorMinuta.moveToFirst())
		    {
		    	tvMinuta.setText(String.valueOf(cursorMinuta.getInt(cursorMinuta.getColumnIndex("sta_minuta"))));
		    }
		   
		    else
		    	tvMinuta.setText("1");
		    //baza.updateMecz(meczID, "0", "0");
		    tvBrGospodarz.setText("0");
	    	tvBrGosc.setText("0");
	    	
	    	Cursor cursorDoliczony = baza.getStatystykiMeczNazwa(meczID, "doliczonyCzas");
	    	if(cursorDoliczony!=null && cursorDoliczony.moveToFirst())
	    	{
	    		doliczoneMinuty = cursorDoliczony.getInt(cursorDoliczony.getColumnIndex("sta_minuta"));
	    	}
	    	else
	    		doliczoneMinuty = 0;
	    	
		    Cursor cPodstawowi = baza.getZawodnicyPodstawowi(meczID);
		    Cursor cRezerwowi = baza.getZawodnicyMecz(meczID);
	    	//cursorPodstawowi = baza.getStatystykiMeczNazwa(meczID, "podstawowy");
		    //cursorRezerwowi = baza.getStatystykiMeczNazwa(meczID, "rezerwowy");

		    if (cPodstawowi!=null && cPodstawowi.moveToFirst())
		    {
		    	do
		    	{
			    		ZawodnikMecz zaw = new ZawodnikMecz();
			    		zaw.setId(cPodstawowi.getLong(0));
			    		zaw.setImie(cPodstawowi.getString(cPodstawowi.getColumnIndex("zaw_imie")));
			    		zaw.setNazwisko(cPodstawowi.getString(cPodstawowi.getColumnIndex("zaw_nazwisko")));
			    		podstawowi.add(zaw);
		    	}
		    	while (!cPodstawowi.isLast() && cPodstawowi.moveToNext());
		    }
		    
		    
		    if (cRezerwowi!=null && cRezerwowi.moveToFirst())
		    {
		    	do
		    	{
		    			boolean zawiera = false;
			    		ZawodnikMecz zaw = new ZawodnikMecz();
			    		zaw.setId(cRezerwowi.getLong(0));
			    		zaw.setImie(cRezerwowi.getString(cRezerwowi.getColumnIndex("zaw_imie")));
			    		zaw.setNazwisko(cRezerwowi.getString(cRezerwowi.getColumnIndex("zaw_nazwisko")));
			    		for(ZawodnikMecz z: podstawowi)
			    		{
			    			if(z.getId()==zaw.getId())
			    				zawiera = true;		
			    		}
			    		
			    		if(!zawiera)
			    			rezerwowi.add(zaw);
		    	}
		    	while (!cRezerwowi.isLast() && cRezerwowi.moveToNext());
		    }
		    
		    
		    
		   // PrzepiszZawodnikow(cursorPodstawowi, cursorZawodnik, podstawowi);
		   //  PrzepiszZawodnikow(cursorRezerwowi, cursorZawodnik, rezerwowi);
		    
		    cursorMecz = baza.getMecz(meczID);
		    if (cursorMecz!=null && cursorMecz.moveToFirst())
		    {

		    	gospodarzID = cursorMecz.getLong(cursorMecz.getColumnIndex("mecz_dru_id_gospodarz"));
		    	goscID = cursorMecz.getLong(cursorMecz.getColumnIndex("mecz_dru_id_gosc"));
		    	
		    	cursorGospodarz = baza.getNazwyDruzyn(gospodarzID);
		    	cursorGosc = baza.getNazwyDruzyn(goscID);
		    	
		    	cursorMojaDruzyna = baza.getDaneKlubu();
				if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst() && cursorMecz!=null)
				{
					mojaDruzynaID = cursorMojaDruzyna.getLong(cursorMojaDruzyna.getColumnIndex("dan_dru_id"));
					
					Cursor c = baza.getStatystykiMeczNazwa(meczID, "bramka");
					if(c!=null && c.moveToFirst())
					{
						if(mojaDruzynaID == gospodarzID)
						{
							int bGosp = c.getCount();
							tvBrGospodarz.setText(String.valueOf(bGosp));
						}
						else if(mojaDruzynaID == goscID)
						{
							int bGosc = c.getCount();
							tvBrGosc.setText(String.valueOf(bGosc));
						}
					}
					
					Cursor p = baza.getStatystykiMeczNazwa(meczID, "bramkaprzeciwnik");
					if(p!=null && p.moveToFirst())
					{
						if(mojaDruzynaID == goscID)
						{
							int bGosp = p.getCount();
							tvBrGospodarz.setText(String.valueOf(bGosp));
						}
						else if(mojaDruzynaID == gospodarzID)
						{
							int bGosc = p.getCount();
							tvBrGosc.setText(String.valueOf(bGosc));
						}
					}
				}
		    	
		    	if(cursorGospodarz!=null && cursorGospodarz.moveToFirst())
		    	{
		    		tvGospodarz.setText(cursorGospodarz.getString(cursorGospodarz.getColumnIndex("dru_nazwa")));
		    	}
		    	else
		    	{
		    		tvGospodarz.setText("---");
		    	}
		    	
		    	if(cursorGosc!=null && cursorGosc.moveToFirst())
		    	{
		    		tvGosc.setText(cursorGosc.getString(cursorGosc.getColumnIndex("dru_nazwa")));
		    	}
		    	else
		    	{
		    		tvGosc.setText("---");
		    	}
		    }
		    CzyMoznaZakonczyc = true;
		}
		
		
		podstawowiAdapter = new RozegrajArrayAdapter(this, R.layout.lv_rozegraj_podstawowi_layout, podstawowi);
		lvPodstawowi.setAdapter(podstawowiAdapter);
		rezerwowiAdapter = new RozegrajArrayAdapter(this, R.layout.lv_rozegraj_podstawowi_layout, rezerwowi);
		lvRezerwowi.setAdapter(rezerwowiAdapter);
		
		lvPodstawowi.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
				//Toast.makeText(getApplicationContext(), lvPodstawowi.getSelectedItemPosition() + " " + position, Toast.LENGTH_SHORT).show();
				if(pozycjaPodstawowi!=position)
				{
					view.setSelected(true);
					pozycjaPodstawowi = position;
					zmianaPodstawowy = (ZawodnikMecz)lvPodstawowi.getItemAtPosition(position);
					
				}

				else
				{
					lvPodstawowi.clearChoices();
					pozycjaPodstawowi = -2;
					zmianaPodstawowy = null;
				}
					
				
			}
		});
		
		
		lvRezerwowi.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long arg3) {
				
				
					if(pozycjaRezerwowi!=position)
					{
						view.setSelected(true);
						pozycjaRezerwowi = position;
						zmianaRezerwowy = (ZawodnikMecz)lvRezerwowi.getItemAtPosition(position);
					}
	
					else
					{
						lvRezerwowi.clearChoices();
						pozycjaRezerwowi = -2;
						zmianaRezerwowy = null;
					}
				
					
				
			}
		});
		

	}
	
	
	
	
	
	private void PrzepiszZawodnikow(Cursor zawodnicy, Cursor z, ArrayList<ZawodnikMecz> lista)
	{
		if (zawodnicy!=null && zawodnicy.moveToFirst())
	    {
	    	do
	    	{
	    		z = baza.getZawodnicy(zawodnicy.getLong(zawodnicy.getColumnIndex("sta_zaw_id")));
	    		if(z!=null && z.moveToFirst())
	    		{
		    		ZawodnikMecz zaw = new ZawodnikMecz();
		    		zaw.setId(z.getLong(z.getColumnIndex("_id")));
		    		zaw.setImie(z.getString(z.getColumnIndex("zaw_imie")));
		    		zaw.setNazwisko(z.getString(z.getColumnIndex("zaw_nazwisko")));
		    		lista.add(zaw);
	    		}
	    	}
	    	while (!zawodnicy.isLast() && zawodnicy.moveToNext());
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mecz_rozegraj, menu);
		return true;
	}
	
	private void PrzypiszId()
	{
		lvPodstawowi = (ListView)findViewById(R.id.lvMeczRozegrajPodstawowi);
		lvRezerwowi = (ListView)findViewById(R.id.lvMeczRozegrajRezerwowi);
		btTyl = (Button)findViewById(R.id.btMeczRozegrajTyl);
		btPrzod = (Button)findViewById(R.id.btMeczRozegrajPrzod);
		btZakoncz = (Button)findViewById(R.id.btMeczRozegrajZakoncz);
		btPrzod5 = (Button)findViewById(R.id.btMeczRozegrajPrzod5);
		btTyl5 = (Button)findViewById(R.id.btMeczRozegrajTyl5);
		btStatystyki = (Button)findViewById(R.id.btMeczRozegrajStatystyki);
		btBramkaPrzeciwnik= (Button)findViewById(R.id.btMeczRozegrajBrPrzeciwnik);
		tvGospodarz = (TextView)findViewById(R.id.tvMeczRozegrajGospodarz);
		tvGosc = (TextView)findViewById(R.id.tvMeczRozegrajGosc);
		tvBrGospodarz = (TextView)findViewById(R.id.tvMeczRozegrajBrGospodarz);
		tvBrGosc = (TextView)findViewById(R.id.tvMeczRozegrajBrGosc);
		tvMinuta = (TextView)findViewById(R.id.tvMeczRozegrajMinuta);
		
		btZmiana = (Button)findViewById(R.id.btMeczRozegrajZmiana);
		btTyl.setOnClickListener(this);
		btPrzod.setOnClickListener(this);
		btPrzod5.setOnClickListener(this);
		btZakoncz.setOnClickListener(this);
		btStatystyki.setOnClickListener(this);
		btBramkaPrzeciwnik.setOnClickListener(this);
		btTyl5.setOnClickListener(this);
		btZmiana.setOnClickListener(this);
		
		
	}
	
	@Override
    protected void onDestroy() {
		super.onDestroy();
        if(baza != null)
            baza.close();
        
    }

	@Override
	public void onClick(View v) {
		int minuta = Integer.valueOf(tvMinuta.getText().toString());
		Intent intent;
		switch (v.getId())
		{
		case R.id.btMeczRozegrajTyl:
			
			if(minuta>0)
			{
				minuta--;
				tvMinuta.setText(String.valueOf(minuta));
			}
				
			break;
		case R.id.btMeczRozegrajTyl5:
			if(minuta>=5)
			{
				minuta=minuta-5;
				tvMinuta.setText(String.valueOf(minuta));
			}
			break;
			
		case R.id.btMeczRozegrajPrzod:

				if(minuta<90)
				{
					minuta++;
					tvMinuta.setText(String.valueOf(minuta));

				}
				else
				{
					if(doliczoneMinuty<=0)
					{
						WyswietlDialogDoliczone();
					}
					else
					{
						if(minuta<90+doliczoneMinuty)
						{
							minuta++;
							tvMinuta.setText(String.valueOf(minuta));
						}
					}
				}

			
			break;
			
		case R.id.btMeczRozegrajPrzod5:
			
			if(minuta<=85)
			{
				minuta += 5;
				tvMinuta.setText(String.valueOf(minuta));
			}
			
			else
			{
				if(doliczoneMinuty<=0)
				{
					WyswietlDialogDoliczone();
				}
				else
				{
					if(minuta<85+doliczoneMinuty)
					{
						minuta+=5;
						tvMinuta.setText(String.valueOf(minuta));
					}
				}
			}
			break;
			
		case R.id.btMeczRozegrajZakoncz:
			
					Zakoncz();
					baza.insertStatystyka(meczID, -1, "koniec", Integer.valueOf(tvMinuta.getText().toString()));
					intent = new Intent(context, Terminarz.class);
					finish();
					startActivity(intent);
					break;
		case R.id.btMeczRozegrajStatystyki:
			
				intent = new Intent(context, MeczStatystyki.class);
				intent.putExtra("mecz", meczID);
				startActivity(intent);
			break;
			
		case R.id.btMeczRozegrajBrPrzeciwnik:
			if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst() && cursorMecz!=null && cursorMecz.moveToFirst())
			{
				
				if(mojaDruzynaID == goscID)
				{
					int bGosp = Integer.valueOf(tvBrGospodarz.getText().toString());
					tvBrGospodarz.setText(String.valueOf(bGosp+1));
				}
				else if(mojaDruzynaID == gospodarzID)
				{
					int bGosc = Integer.valueOf(tvBrGosc.getText().toString());
					tvBrGosc.setText(String.valueOf(bGosc+1));
				}
				
				baza.insertStatystyka(meczID, -1, "bramkaprzeciwnik", Integer.valueOf(tvMinuta.getText().toString()));
			}
			break;
		case R.id.btMeczRozegrajZmiana:
			
				if(zmianaPodstawowy!=null && zmianaRezerwowy!=null)
				{
					if(iloscZmian<4)
					{
						
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				        alertDialog.setTitle("Zmiana");
				        alertDialog.setMessage("Nie istnieje mo¿liwosæ cofniêcia zmiany. Czy na pewno chcesz kontynuowaæ?");
				        alertDialog.setPositiveButton("Kontynuuj", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog,int which) {
				            	
								ZawodnikMecz zp = (ZawodnikMecz)lvPodstawowi.getItemAtPosition(pozycjaPodstawowi);
								podstawowiAdapter.remove(zp);
								podstawowiAdapter.add(zmianaRezerwowy);
								ZawodnikMecz zr = (ZawodnikMecz)lvRezerwowi.getItemAtPosition(pozycjaRezerwowi);
								rezerwowiAdapter.remove(zr);
								rezerwowiAdapter.add(zmianaPodstawowy);
								
								baza.insertStatystyka(meczID, zp.getId(), "zejscie", Integer.valueOf(tvMinuta.getText().toString()));
								baza.insertStatystyka(meczID, zr.getId(), "wejscie", Integer.valueOf(tvMinuta.getText().toString()));
								baza.insertStatystyka(meczID, zp.getId(), "zmianazejscie", Integer.valueOf(tvMinuta.getText().toString()));
								baza.insertStatystyka(meczID, zr.getId(), "zmianawejscie", Integer.valueOf(tvMinuta.getText().toString()));
								
								zmianaPodstawowy = null;
								zmianaRezerwowy = null;
								lvPodstawowi.clearChoices();
								pozycjaPodstawowi = -2;
								lvRezerwowi.clearChoices();
								pozycjaRezerwowi = -2;
								
								
								cursorZmiany = baza.getStatystykiMeczNazwa(meczID, "zmianazejscie");
								 if(cursorZmiany!=null && cursorZmiany.moveToFirst())
								    {
								    	iloscZmian = cursorZmiany.getCount();
								    }
									
				            	
				            }
				        });
		
				        alertDialog.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {
				            dialog.cancel();
				            }
				        });
				        
				        alertDialog.show();
				        

						}
					else
						Toast.makeText(context, "Wykorzystane wszystkie zmiany", Toast.LENGTH_SHORT).show();
				}
				
				else
					Toast.makeText(context, "Wybierz zawodników do zmiany", Toast.LENGTH_SHORT).show();
			
			
			
			break;
			
		}
		
		
			
		
	}
	
	
	private void WyswietlDialogDoliczone()
	{
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.doliczone_dialog_layout, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.etDoliczoneDialog);

		// set dialog message
		alertDialogBuilder
			.setCancelable(false)
			.setPositiveButton("Zapisz",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
			    	
			    	doliczoneMinuty = Integer.valueOf(userInput.getText().toString());
			    	baza.insertStatystyka(meczID, -1, "doliczonyCzas", doliczoneMinuty);
			    	
			    }
			  })
			.setNegativeButton("Anuluj",
			  new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog,int id) {
				dialog.cancel();
			    }
			  });

		// create alert dialog
		alertDialogBuilder.setTitle("Wpisz liczbê doliczonych minut");
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
	
	
	private void Zakoncz()
	{
		if(CzyMoznaZakonczyc)
		{
			
			Cursor wszyscyZawodnicy = baza.getZawodnicy();
			if(wszyscyZawodnicy!=null && wszyscyZawodnicy.moveToFirst())
			{
				do
				{
					long id = wszyscyZawodnicy.getLong(wszyscyZawodnicy.getColumnIndex("_id"));
					Cursor w = baza.getWykluczeniaZawodnikAktywne(id);
					if(w!=null && w.moveToFirst())
					{
						do
						{
							long idW = w.getLong(w.getColumnIndex("_id"));
							int ilMeczow = w.getInt(w.getColumnIndex("wyk_il_meczow")) - 1;
							int aktywne = 1;
							if(ilMeczow<=0)
								aktywne = 0;
			
							baza.updateWykluczenie(idW, aktywne, ilMeczow);
						}
						while(!w.isLast() && w.moveToNext());
					}
					
				}
				while (!wszyscyZawodnicy.isLast() && wszyscyZawodnicy.moveToNext());
			}
			
			
				String bramkiGosp = tvBrGospodarz.getText().toString();
				String bramkiGosc = tvBrGosc.getText().toString();
				int gospodarzMecze, gospodarzPunkty, gospodarzZwyciestwa, gospodarzRemisy, gospodarzPorazki,gospodarzZdobyte, gospodarzStracone,
					goscMecze, goscPunkty,goscZwyciestwa, goscRemisy, goscPorazki,goscZdobyte, goscStracone, brGosp,brGosc;
				
				if(!bramkiGosp.equals("") && !bramkiGosc.equals(""))
				{
					brGosp = Integer.valueOf(bramkiGosp);
					brGosc = Integer.valueOf(bramkiGosc);
					if(cursorGospodarz!=null && cursorGospodarz.moveToFirst())
					{
						gospodarzMecze = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_mecze")) + 1;
						if(brGosp>brGosc)
						{
							gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa")) + 1;
							gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy"));
							gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki"));
							gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty")) + 3;
						}
						else if (brGosp==brGosc)
						{
							gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy")) + 1;
							gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa"));
							gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki"));
							gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty")) + 1;
						}
						else
						{
							gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki")) + 1;
							gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy"));
							gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa"));
							gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty"));
						}
						
						gospodarzZdobyte = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_bramki_zdobyte")) + brGosp;
						gospodarzStracone = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_bramki_stracone")) + brGosc;
						
						baza.updateDruzyna(gospodarzID,gospodarzMecze, gospodarzPunkty, gospodarzZwyciestwa, gospodarzRemisy, gospodarzPorazki,gospodarzZdobyte, gospodarzStracone);
						
					}
					
					if(cursorGosc!=null && cursorGosc.moveToFirst())
					{
						goscMecze = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_mecze")) + 1;
						if(brGosc>brGosp)
						{
							goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa")) + 1;
							goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy"));
							goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki"));
							goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty")) + 3;
						}
						else if (brGosp==brGosc)
						{
							goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy")) + 1;
							goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa"));
							goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki"));
							goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty")) + 1;
						}
						else
						{
							goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki")) + 1;
							goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy"));
							goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa"));
							goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty"));
						}
						
						goscZdobyte = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_bramki_zdobyte")) + brGosc;
						goscStracone = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_bramki_stracone")) + brGosp;
						
						baza.updateDruzyna(goscID, goscMecze, goscPunkty,goscZwyciestwa, goscRemisy, goscPorazki,goscZdobyte, goscStracone);
						
					}
				}
			
				
				baza.updateMecz(meczID, bramkiGosp, bramkiGosc);
				
				
				
				for (ZawodnikMecz zaw : podstawowi)
				{
					baza.insertStatystyka(meczID, zaw.getId(), "zejscie", Integer.valueOf(tvMinuta.getText().toString()));
					Cursor cursorWejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "wejscie", zaw.getId());
					if(cursorWejscie!=null && cursorWejscie.moveToFirst())
					{
						int czas = Integer.valueOf(tvMinuta.getText().toString()) - cursorWejscie.getInt(cursorWejscie.getColumnIndex("sta_minuta")) + 1;
						baza.insertStatystyka(meczID, zaw.getId(), "czasgry", czas);
					}
				}
				
				for (ZawodnikMecz zaw : rezerwowi)
				{
					Cursor cursorZejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "zejscie", zaw.getId());
					if(cursorZejscie!=null && cursorZejscie.moveToFirst())
					{
						Cursor cursorWejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "wejscie", zaw.getId());
						if(cursorWejscie!=null && cursorWejscie.moveToFirst())
						{
							int czas = cursorZejscie.getInt(cursorZejscie.getColumnIndex("sta_minuta"))- cursorWejscie.getInt(cursorWejscie.getColumnIndex("sta_minuta")) + 1;
							baza.insertStatystyka(meczID, zaw.getId(), "czasgry", czas);
						}
					}
					else
					baza.insertStatystyka(meczID, zaw.getId(), "czasgry", 0);
				}
				
				Cursor zawodnik;
				cursorStatystyki = baza.getStatystykiMeczu(meczID);
				if(cursorStatystyki!=null && cursorStatystyki.moveToFirst())
				{
					
					do
					{
						long zawodnikID = cursorStatystyki.getLong(cursorStatystyki.getColumnIndex("sta_zaw_id"));
						String nazwa = cursorStatystyki.getString(cursorStatystyki.getColumnIndex("sta_nazwa"));
						int min = cursorStatystyki.getInt(cursorStatystyki.getColumnIndex("sta_minuta"));
						int mecze,minuty,zKartki,czKartki,bramki;
						if(zawodnikID >= 0)
						{
							zawodnik = baza.getZawodnicy(zawodnikID);
							if(zawodnik!=null && zawodnik.moveToFirst())
							{
								if(nazwa.equals("czasgry") && min>0)
								{
									minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty")) + min;
									mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze")) + 1;
									zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
									czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
									bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
									
									
								}

								else if(nazwa.equals("zolta"))
								{
									minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
									mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
									zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki")) + 1;
									czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
									bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
								}
								
								else if(nazwa.equals("czerwona"))
								{
									minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
									mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
									zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
									czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki")) + 1;
									bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
									if(cursorSezon!=null && cursorSezon.moveToFirst())
									{	long sezId = cursorSezon.getLong(cursorSezon.getColumnIndex("_id"));
										baza.insertWykluczenie(sezId, meczID, zawodnikID, 1, 1, -1);
									}
								}
								
								else if(nazwa.equals("bramka"))
								{
									minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
									mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
									zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
									czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
									bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki")) + 1;
								}
								
								else
								{
									minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
									mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
									zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
									czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
									bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
								}
								
								baza.updateZawodnikStatystyki(zawodnikID, mecze, minuty, zKartki, czKartki, bramki);
								
								
							}
						}
					}
					while (!cursorStatystyki.isLast() && cursorStatystyki.moveToNext());
				}
				
				
				
				
				for (ZawodnikMecz zaw : podstawowi)
				{
					zawodnik = baza.getZawodnicy(zaw.getId());
					int liczbaZKartek = 0;
					if(zawodnik!=null && zawodnik.moveToFirst())
						liczbaZKartek = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
					
					for (int i=0;i<wykluczeniaPoIlu.size();i++)
					{
						Cursor wyk = baza.getWykluczeniaZawodnikPoIlu(zaw.getId(), wykluczeniaPoIlu.get(i));
						if(wyk!=null && !wyk.moveToFirst())
						{
							int maks = 100;
							if(i+1<wykluczeniaPoIlu.size())
								maks=wykluczeniaPoIlu.get(i+1);
							if(liczbaZKartek>=wykluczeniaPoIlu.get(i) && liczbaZKartek<maks)
							{
								Cursor wykluczenieZawodnikMecz = baza.getWykluczeniaZawodnikMecz(zaw.getId(), meczID);
								if(wykluczenieZawodnikMecz!=null && wykluczenieZawodnikMecz.moveToFirst())
								{
									long id = wykluczenieZawodnikMecz.getLong(wykluczenieZawodnikMecz.getColumnIndex("_id"));
									int ilMeczow = wykluczenieZawodnikMecz.getInt(wykluczenieZawodnikMecz.getColumnIndex("wyk_il_meczow")) + 1;
									baza.updateWykluczenie(id, sezonID, meczID, zaw.getId(), 1, ilMeczow, wykluczeniaPoIlu.get(i));
								}
								else if(wykluczenieZawodnikMecz!=null && !wykluczenieZawodnikMecz.moveToFirst())
									baza.insertWykluczenie(sezonID, meczID, zaw.getId(), 1, 1, wykluczeniaPoIlu.get(i));
							}
						}

					}
					
				}
				
				for (ZawodnikMecz zaw : rezerwowi)
				{
					
				}
				
			
		}
	}
	
	
	
	
	
	
	
	
	private class RozegrajArrayAdapter extends ArrayAdapter<ZawodnikMecz>
	{
		private Activity context;
		public RozegrajArrayAdapter(Activity context, int layout, List<ZawodnikMecz> lista) {
			super(context, layout,lista);
			this.context = context;

		}
		
		class ViewHolder {
	        public TextView tvImie, tvNazwisko;
	        public Button btZKartka, btCzKartka, btBramka;
	     
	    }
		
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
			final ViewHolder vHolder;
			View rowView = convertView;
			final ZawodnikMecz zawodnik = getItem(position);
			if(rowView == null)
			{
				LayoutInflater layoutInflater = context.getLayoutInflater();
		        rowView =  layoutInflater.inflate(R.layout.lv_rozegraj_podstawowi_layout, null, true);
		        vHolder = new ViewHolder();
		        vHolder.tvImie = (TextView) rowView.findViewById(R.id.tvRozegrajImie);
		        vHolder.tvNazwisko = (TextView) rowView.findViewById(R.id.tvRozegrajNazwisko); 
		        vHolder.btZKartka = (Button) rowView.findViewById(R.id.btRozegrajZKartki);
		        vHolder.btCzKartka = (Button) rowView.findViewById(R.id.btRozegrajCzKartki);
		        vHolder.btBramka = (Button) rowView.findViewById(R.id.btRozegrajBramki);
		        
		        rowView.setTag(vHolder);
	        } 
			
			else 
	            vHolder = (ViewHolder) rowView.getTag();
			

			vHolder.tvImie.setText(zawodnik.getImie());
			vHolder.tvNazwisko.setText(zawodnik.getNazwisko());
			
			Cursor bramki = baza.getStatystykiMeczNazwaZawodnik(meczID, "bramka", zawodnik.getId());
			if(bramki !=null && bramki.moveToFirst())
			{
				int liczbaBramek = bramki.getCount();
				vHolder.btBramka.setText(String.valueOf(liczbaBramek));
			}
			
			Cursor zKartki = baza.getStatystykiMeczNazwaZawodnik(meczID, "zolta", zawodnik.getId());
			if(zKartki !=null && zKartki.moveToFirst())
			{
				int liczbaZKartek = zKartki.getCount();
				vHolder.btZKartka.setText(String.valueOf(liczbaZKartek));
			}
			
			int wej = 0, zej = 0;
			Cursor wejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "wejscie", zawodnik.getId());
			Cursor zejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "zejscie", zawodnik.getId());
			if(wejscie!=null)
				wej = wejscie.getCount();
			if(zejscie!=null)
				zej = zejscie.getCount();
			
			Cursor czKartki = baza.getStatystykiMeczNazwaZawodnik(meczID, "czerwona", zawodnik.getId());
			if(czKartki !=null)
			{
				
				
				int liczbaCzKartek = czKartki.getCount();
				vHolder.btCzKartka.setText(String.valueOf(liczbaCzKartek));
				if(!(liczbaCzKartek>=1) && wej>0 && zej<=0)
				{
					vHolder.btCzKartka.setEnabled(true);
					vHolder.btZKartka.setEnabled(true);
					vHolder.btBramka.setEnabled(true);
				}
				
				else
				{

					vHolder.btCzKartka.setEnabled(false);
					vHolder.btZKartka.setEnabled(false);
					vHolder.btBramka.setEnabled(false);
				}
				
				
				if(liczbaCzKartek>=1 || (wej>0 && zej>0))
				{
					vHolder.tvImie.setTextColor(Color.GRAY);
					vHolder.tvNazwisko.setTextColor(Color.GRAY);
				}
				
				else
				{
					vHolder.tvImie.setTextColor(Color.WHITE);
					vHolder.tvNazwisko.setTextColor(Color.WHITE);
				}
			}
			
			
			
			
			vHolder.btZKartka.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Button bt = (Button)v;
					bt.setText(String.valueOf(Integer.valueOf(bt.getText().toString())+1));
					int ilosc = Integer.valueOf(bt.getText().toString());
					baza.insertStatystyka(meczID, zawodnik.getId(), "zolta", Integer.valueOf(tvMinuta.getText().toString()));
					if(ilosc>=2)
					{
						vHolder.btCzKartka.setEnabled(false);
						vHolder.btZKartka.setEnabled(false);
						vHolder.btBramka.setEnabled(false);
						vHolder.btCzKartka.setText(String.valueOf(Integer.valueOf(vHolder.btCzKartka.getText().toString())+1));
						baza.insertStatystyka(meczID, zawodnik.getId(), "czerwona", Integer.valueOf(tvMinuta.getText().toString()));
						Toast.makeText(context, "Zawodnik " + zawodnik.getImie() + " " + zawodnik.getNazwisko() + 
								" otrzyma³ czerwon¹ kartkê w wyniku dwóch ¿ó³tych kartek " + tvMinuta.getText().toString() + " minucie", Toast.LENGTH_SHORT).show();
					}
					
					else
					{
						
							vHolder.btCzKartka.setEnabled(true);
							vHolder.btZKartka.setEnabled(true);
							vHolder.btBramka.setEnabled(true);
						
						Toast.makeText(context, "Zawodnik " + zawodnik.getImie() + " " + zawodnik.getNazwisko() + 
						" otrzyma³ ¿ó³t¹ kartkê " + tvMinuta.getText().toString() + " minucie", Toast.LENGTH_SHORT).show();
					}
					
					
					
					notifyDataSetChanged();
					
					
				}
			});
			
			vHolder.btCzKartka.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						Button bt = (Button)v;
						bt.setText(String.valueOf(Integer.valueOf(bt.getText().toString())+1));
						
						if(Integer.valueOf(bt.getText().toString())>=1)
						{
							vHolder.btCzKartka.setEnabled(false);
							vHolder.btZKartka.setEnabled(false);
							vHolder.btBramka.setEnabled(false);
						}
						else
						{
							vHolder.btCzKartka.setEnabled(true);
							vHolder.btZKartka.setEnabled(true);
							vHolder.btBramka.setEnabled(true);
						}
						
						baza.insertStatystyka(meczID, zawodnik.getId(), "czerwona", Integer.valueOf(tvMinuta.getText().toString()));
						Toast.makeText(context, "Zawodnik " + zawodnik.getImie() + " " + zawodnik.getNazwisko() + 
								" otrzyma³ czerwon¹ kartkê w " + tvMinuta.getText().toString() + " minucie", Toast.LENGTH_SHORT).show();
						
						notifyDataSetChanged();
					}
			});

			vHolder.btBramka.setOnClickListener(new OnClickListener() {
		
				@Override
				public void onClick(View v) {
					
					Button bt = (Button)v;
					
					bt.setText(String.valueOf(Integer.valueOf(bt.getText().toString())+1));
					
					
					if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst() && cursorMecz!=null && cursorMecz.moveToFirst())
					{
						
						if(mojaDruzynaID == gospodarzID)
						{
							int bGosp = Integer.valueOf(tvBrGospodarz.getText().toString());
							tvBrGospodarz.setText(String.valueOf(bGosp+1));
						}
						else if(mojaDruzynaID == goscID)
						{
							int bGosc = Integer.valueOf(tvBrGosc.getText().toString());
							tvBrGosc.setText(String.valueOf(bGosc+1));
						}
					}
					
					baza.insertStatystyka(meczID, zawodnik.getId(), "bramka", Integer.valueOf(tvMinuta.getText().toString()));
					Toast.makeText(context, "Zawodnik " + zawodnik.getImie() + " " + zawodnik.getNazwisko() + 
							" zdoby³ bramkê w " + tvMinuta.getText().toString() + " minucie", Toast.LENGTH_SHORT).show();
					
					notifyDataSetChanged();
				}
			});
	        
			return rowView;
		}
		
		@Override
		public boolean isEnabled(int position) {
			final ZawodnikMecz zawodnik = getItem(position);
			int wej = 0, zej = 0;
			Cursor wejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "wejscie", zawodnik.getId());
			Cursor zejscie = baza.getStatystykiMeczNazwaZawodnik(meczID, "zejscie", zawodnik.getId());
			if(wejscie!=null)
				wej = wejscie.getCount();
			if(zejscie!=null)
				zej = zejscie.getCount();
			
			Cursor czKartki = baza.getStatystykiMeczNazwaZawodnik(meczID, "czerwona", zawodnik.getId());
			if(czKartki !=null)
			{
				
				
				int liczbaCzKartek = czKartki.getCount();
				if(liczbaCzKartek>=1 || (wej>0 && zej>0))
				{
					
					return false;
					
					
				}
				
				else
				{
					
					return true;
					
				}
			}
			return false;
		}
		

		
	}

}





/* ZWIÊKSZANIE WYNIKU btBramka
cursorMojaDruzyna = baza.getDaneKlubu();
if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst() && cursorMecz!=null && cursorMecz.moveToFirst())
{
	mojaDruzynaID = cursorMojaDruzyna.getLong(cursorMojaDruzyna.getColumnIndex("dan_dru_id"));
	String bramkiGospodarz, bramkiGosc;
	bramkiGospodarz = cursorMecz.getString(cursorMecz.getColumnIndex("mecz_bramkigosp"));
	bramkiGosc = cursorMecz.getString(cursorMecz.getColumnIndex("mecz_bramkigosc"));
	if(mojaDruzynaID == gospodarzID)
	{
		String br = String.valueOf(Integer.valueOf(bramkiGospodarz)+1);
		baza.updateMecz(meczID, br, bramkiGosc);
		
		if(cursorMecz.requery() && cursorMecz.moveToFirst())
		tvBrGospodarz.setText(cursorMecz.getString(cursorMecz.getColumnIndex("mecz_bramkigosp")));
	}
	else if(mojaDruzynaID == goscID)
	{
		String br = String.valueOf(Integer.valueOf(bramkiGosc)+1);
		baza.updateMecz(meczID, bramkiGospodarz, br);

		if(cursorMecz.requery() && cursorMecz.moveToFirst())
			tvBrGosc.setText(cursorMecz.getString(cursorMecz.getColumnIndex("mecz_bramkigosc")));
	}
	
	bt.setText(String.valueOf(Integer.valueOf(bt.getText().toString())+1));
}
cursorMecz.requery();*/


