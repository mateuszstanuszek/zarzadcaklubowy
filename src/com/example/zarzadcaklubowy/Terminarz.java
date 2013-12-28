package com.example.zarzadcaklubowy;

import java.util.ArrayList;

import com.example.zarzadcaklubowy.MyCursorAdapter.ViewHolder;

import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class Terminarz extends Activity implements OnClickListener{
	
	private BazaDanych baza;
	private Context context;
	private Cursor cursor, cursorUsun, cursorGospodarz, cursorGosc, cursorStatystyki;
	private ListView lvTerminarz;
	private TerminarzCursorAdapter dataAdapter;
	private Button btDodajMecz, btWprowadzWyniki,btUsunZaznaczone;
	private Intent intent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terminarz);
		lvTerminarz = (ListView)findViewById(R.id.lvTerminarz);
		btDodajMecz = (Button)findViewById(R.id.btTerminarzDodajMecz);
		btWprowadzWyniki = (Button)findViewById(R.id.btTerminarzWprowadzWyniki);
		btUsunZaznaczone = (Button)findViewById(R.id.btTerminarzUsun);
		btUsunZaznaczone.setOnClickListener(this);
		btDodajMecz.setOnClickListener(this);
		btWprowadzWyniki.setOnClickListener(this);
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		//baza.insertMecz(39,5,6,1,2,3,"2012-11-11","20:00:00");
		//baza.insertMecz(39,6,5,2,1,7);
		//baza.insertMecz(sez_id, gosp_id, gosc_id, kolejka, bramkigosp, bramkigosc)
		
		PobierzTerminarz();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terminarz, menu);
		return true;
	}
	
	private void PobierzTerminarz()
	{
		String [] kolumny = new String[]{"mecz_kolejka","mecz_dru_id_gospodarz","mecz_bramkigosp",
										"mecz_bramkigosc","mecz_dru_id_gosc","mecz_data","mecz_godzina"};
		int [] to = new int []{R.id.tvTerminarzLayoutKolejka,R.id.tvTerminarzLayoutGospodarz,R.id.tvTerminarzLayoutBrGosp,
				R.id.tvTerminarzLayoutBrGoscie,R.id.tvTerminarzLayoutGosc,R.id.tvTerminarzLayoutData,R.id.tvTerminarzLayoutGodzina};
		
        cursor = baza.getMecze();
        if (cursor!=null && cursor.moveToFirst())
        {
        	dataAdapter = new TerminarzCursorAdapter(baza, this, R.layout.lv_terminarz_layout, cursor, kolumny, to);
        	lvTerminarz.setAdapter(dataAdapter);
        }
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }
	
	@Override
	public void onResume()
	    {  // After a pause OR at startup
	    super.onResume();
	    PobierzTerminarz();
	     }

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btTerminarzDodajMecz:
			intent = new Intent(context, TerminarzDodajMecz.class);
			startActivity(intent);
			break;
		case R.id.btTerminarzWprowadzWyniki:
			intent = new Intent(context, TerminarzWprowadzWyniki.class);
			startActivity(intent);
			break;
		case R.id.btTerminarzUsun:
			for (Integer i : dataAdapter.POZYCJE) {
				
				cursorUsun = (Cursor)lvTerminarz.getItemAtPosition(i);
				long UsunID = cursorUsun.getLong(cursorUsun.getColumnIndex("_id"));
				Cursor cursorMojaDruzyna = baza.getDaneKlubu();
				long mojaDruzynaID = -1;
				
				boolean czyMoznaUsunac = true;
				
				
				
				
				if(cursorUsun!=null)
				{
					
					
					if(czyMoznaUsunac)
					{
							String bramkiGosp = cursorUsun.getString(cursorUsun.getColumnIndex("mecz_bramkigosp"));
							String bramkiGosc = cursorUsun.getString(cursorUsun.getColumnIndex("mecz_bramkigosc"));
							
							int idGospodarz, idGosc;
							idGospodarz = cursorUsun.getInt(cursorUsun.getColumnIndex("mecz_dru_id_gospodarz"));
							idGosc = cursorUsun.getInt(cursorUsun.getColumnIndex("mecz_dru_id_gosc"));
							cursorGospodarz = baza.getNazwyDruzyn(idGospodarz);
							cursorGosc = baza.getNazwyDruzyn(idGosc);
							int gospodarzMecze, gospodarzPunkty, gospodarzZwyciestwa, gospodarzRemisy, gospodarzPorazki,gospodarzZdobyte, gospodarzStracone,
								goscMecze, goscPunkty,goscZwyciestwa, goscRemisy, goscPorazki,goscZdobyte, goscStracone, brGosp,brGosc;
							
							
							if(!bramkiGosp.equals("") && !bramkiGosc.equals(""))
							{
								brGosp = Integer.valueOf(bramkiGosp);
								brGosc = Integer.valueOf(bramkiGosc);
								if(cursorGospodarz!=null && cursorGospodarz.moveToFirst())
								{
									gospodarzMecze = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_mecze")) - 1;
									if(brGosp>brGosc)
									{
										gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa")) - 1;
										gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy"));
										gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki"));
										gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty")) - 3;
									}
									else if (brGosp==brGosc)
									{
										gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy")) - 1;
										gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa"));
										gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki"));
										gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty")) - 1;
									}
									else
									{
										gospodarzPorazki = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_porazki")) - 1;
										gospodarzRemisy = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_remisy"));
										gospodarzZwyciestwa = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_zwyciestwa"));
										gospodarzPunkty = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_punkty"));
									}
									
									gospodarzZdobyte = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_bramki_zdobyte")) - brGosp;
									gospodarzStracone = cursorGospodarz.getInt(cursorGospodarz.getColumnIndex("dru_bramki_stracone")) - brGosc;
									
									baza.updateDruzyna(idGospodarz,gospodarzMecze, gospodarzPunkty, gospodarzZwyciestwa, gospodarzRemisy, gospodarzPorazki,gospodarzZdobyte, gospodarzStracone);
									
								}
								
								if(cursorGosc!=null && cursorGosc.moveToFirst())
								{
									goscMecze = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_mecze")) - 1;
									if(brGosc>brGosp)
									{
										goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa")) - 1;
										goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy"));
										goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki"));
										goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty")) - 3;
									}
									else if (brGosp==brGosc)
									{
										goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy")) - 1;
										goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa"));
										goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki"));
										goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty")) - 1;
									}
									else
									{
										goscPorazki = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_porazki")) - 1;
										goscRemisy = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_remisy"));
										goscZwyciestwa = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_zwyciestwa"));
										goscPunkty = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_punkty"));
									}
									
									goscZdobyte = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_bramki_zdobyte")) - brGosc;
									goscStracone = cursorGosc.getInt(cursorGosc.getColumnIndex("dru_bramki_stracone")) - brGosp;
									
									baza.updateDruzyna(idGosc, goscMecze, goscPunkty,goscZwyciestwa, goscRemisy, goscPorazki,goscZdobyte, goscStracone);
									
								}
							}
					
					
					
					
					
				
					
					
					
					
					Cursor zawodnik;
					cursorStatystyki = baza.getStatystykiMeczu(UsunID);
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
									if(nazwa.equals("czasgry") && min > 0)
									{
										minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty")) - min;
										mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze")) - 1;
										zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
										czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
										bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
										
										
									}

									else if(nazwa.equals("zolta"))
									{
										minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
										mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
										zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki")) - 1;
										czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
										bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
									}
									
									else if(nazwa.equals("czerwona"))
									{
										minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
										mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
										zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
										czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki")) - 1;
										bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki"));
									}
									
									else if(nazwa.equals("bramka"))
									{
										minuty = zawodnik.getInt(zawodnik.getColumnIndex("zaw_minuty"));
										mecze = zawodnik.getInt(zawodnik.getColumnIndex("zaw_mecze"));
										zKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_zolte_kartki"));
										czKartki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_czerw_kartki"));
										bramki = zawodnik.getInt(zawodnik.getColumnIndex("zaw_bramki")) - 1;
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
					
					
					baza.deleteStatystykaMecz(UsunID);
					baza.deleteMecz(UsunID);
					baza.deleteWykluczenie(UsunID);
					}
					

				}
			}
			
			
			
			
			
			
			
			
			
			dataAdapter.POZYCJE.clear();
			if(cursorUsun!=null)
			cursorUsun.requery();
			PobierzTerminarz();
			break;
		}
		
	}
	
	class TerminarzCursorAdapter extends SimpleCursorAdapter
	{
		private Activity context;
		private Cursor cursor, cursorNazwaDruzyny;
		public final ArrayList<Integer> POZYCJE = new ArrayList<Integer>();
		public TerminarzCursorAdapter(BazaDanych baza, Activity context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			this.context = context;
			this.cursor = c;
			
		}
		
		private class ViewHolder {
	        public TextView tvKolejka, tvGospodarz, tvBramkiGosp, tvBramkiGosc, tvGosc, tvData, tvGodzina;
	        public CheckBox chbox;
	        public LinearLayout llWiersz;
	     
	    }
		
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder vHolder;
			View rowView = convertView;
			if(rowView == null)
			{
				LayoutInflater layoutInflater = context.getLayoutInflater();
		        rowView =  layoutInflater.inflate(R.layout.lv_terminarz_layout, null, true);
		        vHolder = new ViewHolder();
		        vHolder.tvKolejka = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutKolejka);
		        vHolder.tvGospodarz = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutGospodarz);
		        vHolder.tvBramkiGosp = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutBrGosp);
		        vHolder.tvBramkiGosc = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutBrGoscie);
		        vHolder.tvGosc = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutGosc);
		        vHolder.tvData = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutData);
		        vHolder.tvGodzina = (TextView)rowView.findViewById(R.id.tvTerminarzLayoutGodzina);
		        vHolder.chbox = (CheckBox) rowView.findViewById(R.id.chbTerminarz);
		        vHolder.llWiersz = (LinearLayout)rowView.findViewById(R.id.llTerminarzWiersz);
		        rowView.setTag(vHolder);
	        } 
			
			else 
	            vHolder = (ViewHolder) rowView.getTag();
	        
			if(position%2 == 0)
				vHolder.llWiersz.setBackgroundColor(Color.parseColor("#3F3F3F"));
			else
				vHolder.llWiersz.setBackgroundColor(Color.parseColor("#232323"));
			
			
	        cursor.moveToPosition(position);
	        
	        vHolder.tvKolejka.setText(cursor.getString(cursor.getColumnIndex("mecz_kolejka")));
	        
	        if(POZYCJE.contains(position))
	        {
	        	vHolder.chbox.setChecked(true);
	        }
	        else
	        {
	        	vHolder.chbox.setChecked(false);
	        }
	        
	        
	        int idGospodarz = cursor.getInt(cursor.getColumnIndex("mecz_dru_id_gospodarz"));
	        cursorNazwaDruzyny = baza.getNazwyDruzyn(idGospodarz);
	        if(cursorNazwaDruzyny!=null && cursorNazwaDruzyny.moveToFirst())
	        	vHolder.tvGospodarz.setText(cursorNazwaDruzyny.getString(cursorNazwaDruzyny.getColumnIndex("dru_nazwa")));
	        else
	        	vHolder.tvGospodarz.setText("---");
	        
	        
	        int idGosc = cursor.getInt(cursor.getColumnIndex("mecz_dru_id_gosc"));
	        cursorNazwaDruzyny = baza.getNazwyDruzyn(idGosc);
	        if(cursorNazwaDruzyny!=null && cursorNazwaDruzyny.moveToFirst())
	        	vHolder.tvGosc.setText(cursorNazwaDruzyny.getString(cursorNazwaDruzyny.getColumnIndex("dru_nazwa")));

	        else
	        	vHolder.tvGosc.setText("---");
	        
	        
	       
	        vHolder.tvBramkiGosp.setText(cursor.getString(cursor.getColumnIndex("mecz_bramkigosp")));
	        
	       
	        vHolder.tvBramkiGosc.setText(cursor.getString(cursor.getColumnIndex("mecz_bramkigosc")));
	        
	        
	        
	        vHolder.tvData.setText(cursor.getString(cursor.getColumnIndex("mecz_data")));
	        vHolder.tvGodzina.setText(cursor.getString(cursor.getColumnIndex("mecz_godzina")));
	        
	        vHolder.chbox.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(POZYCJE.contains(position))
					{
						POZYCJE.remove(Integer.valueOf(position));
						
					}
					else
					{
						POZYCJE.add(position);
					}
					
				}
			});
				


		
			return rowView;
		}
		
		

}


	
}
