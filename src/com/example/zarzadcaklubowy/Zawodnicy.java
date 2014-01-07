package com.example.zarzadcaklubowy;

import java.util.ArrayList;

import com.example.zarzadcaklubowy.TabelaCursorAdapter.ViewHolder;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Zawodnicy extends Activity implements OnClickListener{

	private TextView tvZawodnikImie, tvZawodnikNazwisko, tvZawodnikPesel, tvZawodnikData, tvZawodnikAdres, tvZawodnikTelefon;
	private TextView tvZawodnikEmail, tvZawodnikMecze, tvZawodnikMinuty, tvZawodnikBramki, tvZawodnikZKarki, tvZawodnikCzKartki, tvZawodnikPozycja;
	private Context context;
	private Spinner spinnerStatystyki;
	private ListView lvStatystyki;
	private BazaDanych baza;
	private Cursor cursor, cursorMecze, cursorMojaDruzyna, cursorStatystyki;
	private Intent intent;
	private SpinnerCursorAdapter spinnerAdapter;
	private ZawodnikStatystykiCursorAdapter lvAdapter;
	
	private Button btZawodnikPoprzedni,btZawodnikDodaj, btZawodnikEdytuj, btZawodnikUsun, btZawodnikNastepny;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zawodnicy);
		PrzypiszId();
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		//baza.insertZawodnik(1, "Mateusz", "Stanuszek", "91081104136" ,"Lewniowa 263", "11.08.1991", "663316858", "matus0@vp.pl", "obroñca");
		//baza.insertZawodnik(1, "Amadeusz", "W¹sik", "93543342434" ,"Gnojnik 100", "12.12.1981", "632432438", "lolo@vp.pl", "napastnik");
		PobierzZawodnikow();
		UzupelnijSpinner();
		UzupelnijListView();
		
		spinnerStatystyki.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
				UzupelnijListView();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.zawodnicy, menu);
		return true;
	}
	
	
	private void UzupelnijListView()
	{
		if(cursor!=null && cursor.moveToPosition(cursor.getPosition()))
		{
			long zawId = cursor.getLong(cursor.getColumnIndex("_id"));
			Cursor cursorMecz = (Cursor)spinnerStatystyki.getSelectedItem();
			if(cursorMecz!=null)
			{
				long meczId = cursorMecz.getLong(cursorMecz.getColumnIndex("_id"));
				cursorStatystyki = baza.getStatystykiMeczZawodnik(meczId, zawId);
				if (cursorStatystyki!=null)
				{
					lvAdapter = new ZawodnikStatystykiCursorAdapter(this,cursorStatystyki);
					lvStatystyki.setAdapter(lvAdapter);
				}
			}
		}
	}
	
	private void UzupelnijSpinner()
	{
		cursorMojaDruzyna = baza.getDaneKlubu();
		if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst())
		{
			long mojaDruzynaID = cursorMojaDruzyna.getLong(cursorMojaDruzyna.getColumnIndex("dan_dru_id"));
			cursorMecze = baza.getMeczZDruzyna(mojaDruzynaID);
			
			if(cursorMecze!=null)
			{
				spinnerAdapter = new SpinnerCursorAdapter(this, cursorMecze);
				spinnerStatystyki.setAdapter(spinnerAdapter);
			}
		}
	}
	
	private void PobierzZawodnikow()
	{
		cursor = baza.getZawodnicy();
		
		if(cursor!=null && cursor.moveToFirst())
		{
			tvZawodnikImie.setText(cursor.getString(cursor.getColumnIndex("zaw_imie")));
			tvZawodnikNazwisko.setText(cursor.getString(cursor.getColumnIndex("zaw_nazwisko")));
			tvZawodnikPesel.setText(cursor.getString(cursor.getColumnIndex("zaw_PESEL")));
			tvZawodnikData.setText(cursor.getString(cursor.getColumnIndex("zaw_data")));
			tvZawodnikAdres.setText(cursor.getString(cursor.getColumnIndex("zaw_adres")));
			tvZawodnikTelefon.setText(cursor.getString(cursor.getColumnIndex("zaw_nrtel")));
			tvZawodnikEmail.setText(cursor.getString(cursor.getColumnIndex("zaw_email")));
			tvZawodnikPozycja.setText(cursor.getString(cursor.getColumnIndex("zaw_pozycja")));
			tvZawodnikMecze.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_mecze"))));
			tvZawodnikMinuty.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_minuty"))));
			tvZawodnikBramki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_bramki"))));
			tvZawodnikZKarki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_zolte_kartki"))));
			tvZawodnikCzKartki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_czerw_kartki"))));
			
			
			
			
		}
		else
		{
			tvZawodnikImie.setText("---");
			tvZawodnikNazwisko.setText("---");
			tvZawodnikPesel.setText("---");
			tvZawodnikData.setText("---");
			tvZawodnikAdres.setText("---");
			tvZawodnikTelefon.setText("---");
			tvZawodnikEmail.setText("---");
			tvZawodnikPozycja.setText("---");
			tvZawodnikMecze.setText("-");
			tvZawodnikMinuty.setText("-");
			tvZawodnikBramki.setText("-");
			tvZawodnikZKarki.setText("-");
			tvZawodnikCzKartki.setText("-");
		}
	}
	
	private void PrzypiszId()
	{
		tvZawodnikImie = (TextView)findViewById(R.id.tvZawodnicyImie);
		tvZawodnikNazwisko = (TextView)findViewById(R.id.tvZawodnicyNazwisko);
		tvZawodnikPesel = (TextView)findViewById(R.id.tvZawodnicyPesell);
		tvZawodnikData = (TextView)findViewById(R.id.tvZawodnicyData);
		tvZawodnikAdres = (TextView)findViewById(R.id.tvZawodnicyAdress);
		tvZawodnikTelefon = (TextView)findViewById(R.id.tvZawodnicyTele);
		tvZawodnikEmail = (TextView)findViewById(R.id.tvZawodnicyEmail);
		tvZawodnikMecze = (TextView)findViewById(R.id.tvZawodnicyMecze);
		tvZawodnikMinuty = (TextView)findViewById(R.id.tvZawodnicyMinuty);
		tvZawodnikBramki = (TextView)findViewById(R.id.tvZawodnicyBramki);
		tvZawodnikZKarki = (TextView)findViewById(R.id.tvZawodnicyZKartki);
		tvZawodnikCzKartki = (TextView)findViewById(R.id.tvZawodnicyCzKartki);
		tvZawodnikPozycja = (TextView)findViewById(R.id.tvZawodnicyPozycja);
		lvStatystyki = (ListView)findViewById(R.id.lvZawodnicyStatystyki);
		spinnerStatystyki = (Spinner)findViewById(R.id.spinnerZawodnicy);
		
		btZawodnikDodaj = (Button)findViewById(R.id.btZawodnicyDodaj);
		btZawodnikPoprzedni = (Button)findViewById(R.id.btZawodnicyPoprzedni);
		btZawodnikEdytuj = (Button)findViewById(R.id.btZawodnicyEdytuj);
		btZawodnikUsun = (Button)findViewById(R.id.btZawodnicyUsun);
		btZawodnikNastepny = (Button)findViewById(R.id.btZawodnicyNastepny);
		
		btZawodnikDodaj.setOnClickListener(this);
		btZawodnikPoprzedni.setOnClickListener(this);
		btZawodnikEdytuj.setOnClickListener(this);
		btZawodnikUsun.setOnClickListener(this);
		btZawodnikNastepny.setOnClickListener(this);
		
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }

	@Override
	public void onClick(View v) {

			switch (v.getId())
			{
			case R.id.btZawodnicyPoprzedni:
				if(cursor!=null)
				{
					if(cursor.moveToPrevious())
					{
						tvZawodnikImie.setText(cursor.getString(cursor.getColumnIndex("zaw_imie")));
						tvZawodnikNazwisko.setText(cursor.getString(cursor.getColumnIndex("zaw_nazwisko")));
						tvZawodnikPesel.setText(cursor.getString(cursor.getColumnIndex("zaw_PESEL")));
						tvZawodnikData.setText(cursor.getString(cursor.getColumnIndex("zaw_data")));
						tvZawodnikAdres.setText(cursor.getString(cursor.getColumnIndex("zaw_adres")));
						tvZawodnikTelefon.setText(cursor.getString(cursor.getColumnIndex("zaw_nrtel")));
						tvZawodnikEmail.setText(cursor.getString(cursor.getColumnIndex("zaw_email")));
						tvZawodnikPozycja.setText(cursor.getString(cursor.getColumnIndex("zaw_pozycja")));
						tvZawodnikMecze.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_mecze"))));
						tvZawodnikMinuty.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_minuty"))));
						tvZawodnikBramki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_bramki"))));
						tvZawodnikZKarki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_zolte_kartki"))));
						tvZawodnikCzKartki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_czerw_kartki"))));
						UzupelnijSpinner();
					}
					else cursor.moveToPosition(cursor.getPosition()+1);
							
				}
				
				break;
			case R.id.btZawodnicyDodaj:
				
					intent = new Intent(context, ZawodnicyDodaj.class);
					intent.putExtra("czyEdycja", false);
					startActivity(intent);
				
						
				break;
			case R.id.btZawodnicyEdytuj:
				if (cursor!=null && cursor.moveToPosition(cursor.getPosition()))
				{
					intent = new Intent(context, ZawodnicyDodaj.class);
					intent.putExtra("czyEdycja", true);
					intent.putExtra("id", cursor.getInt(cursor.getColumnIndex("_id")));
					startActivity(intent);
				}
				
				break;
			case R.id.btZawodnicyUsun:
				if(cursor!=null && cursor.moveToPosition(cursor.getPosition()))
            	{
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			        alertDialog.setTitle("Usuwanie zawodnika");
			        alertDialog.setMessage("Czy jesteœ pewien?");
			        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
			            	
			            		int idZawodnik = cursor.getInt(cursor.getColumnIndex("_id"));
			            		baza.deleteZawodnik(idZawodnik); 
			            		PobierzZawodnikow();
			            	
			            }
			        });
	
			        alertDialog.setNegativeButton("ANULUJ", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int which) {
			            dialog.cancel();
			            }
			        });
			        
			        alertDialog.show();
            	}
				
				break;
			case R.id.btZawodnicyNastepny:
				
				if(cursor!=null)
				{
					if (cursor.moveToNext())
					{
						tvZawodnikImie.setText(cursor.getString(cursor.getColumnIndex("zaw_imie")));
						tvZawodnikNazwisko.setText(cursor.getString(cursor.getColumnIndex("zaw_nazwisko")));
						tvZawodnikPesel.setText(cursor.getString(cursor.getColumnIndex("zaw_PESEL")));
						tvZawodnikData.setText(cursor.getString(cursor.getColumnIndex("zaw_data")));
						tvZawodnikAdres.setText(cursor.getString(cursor.getColumnIndex("zaw_adres")));
						tvZawodnikTelefon.setText(cursor.getString(cursor.getColumnIndex("zaw_nrtel")));
						tvZawodnikEmail.setText(cursor.getString(cursor.getColumnIndex("zaw_email")));
						tvZawodnikPozycja.setText(cursor.getString(cursor.getColumnIndex("zaw_pozycja")));
						tvZawodnikMecze.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_mecze"))));
						tvZawodnikMinuty.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_minuty"))));
						tvZawodnikBramki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_bramki"))));
						tvZawodnikZKarki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_zolte_kartki"))));
						tvZawodnikCzKartki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("zaw_czerw_kartki"))));
						UzupelnijSpinner();
					}
					else cursor.moveToPosition(cursor.getPosition()-1);
				}
				break;
			}
			

		
	}
	
	@Override
	public void onResume()
	    {  // After a pause OR at startup
	    super.onResume();
	    PobierzZawodnikow();
	     }
	
	class SpinnerCursorAdapter extends SimpleCursorAdapter
	{
		private Activity context;
		private Cursor cursor;
		public SpinnerCursorAdapter(Activity context, Cursor c) 
		{
			super(context, R.layout.spinner_zawodnicy_layout, c, new String[]{}, new int[]{});
			this.context = context;
			this.cursor = c;
			// TODO Auto-generated constructor stub
		}
		
		private class ViewHolder {
	        public TextView tvGospodarz,tvGosc,tvKolejka;
	    }
		
		@Override
	    public View getDropDownView(int position, View convertView,ViewGroup parent) {
	        return getView(position, convertView, parent);
	    }
		
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder vHolder;
			View rowView = convertView;
			if(rowView == null)
			{
				LayoutInflater layoutInflater = context.getLayoutInflater();
		        rowView =  layoutInflater.inflate(R.layout.spinner_zawodnicy_layout, null, true);
		        vHolder = new ViewHolder();
		        vHolder.tvGospodarz = (TextView)rowView.findViewById(R.id.tvSpinnerGospodarzz);
		        vHolder.tvGosc = (TextView)rowView.findViewById(R.id.tvSpinnerGoscc);
		        vHolder.tvKolejka = (TextView)rowView.findViewById(R.id.tvSpinnerKolejkaa);

		        rowView.setTag(vHolder);
			}
			else 
	            vHolder = (ViewHolder) rowView.getTag();
			
			
			
				cursor.moveToPosition(position);
				long idGospodarz = cursor.getLong(cursor.getColumnIndex("mecz_dru_id_gospodarz"));
				long idGosc = cursor.getLong(cursor.getColumnIndex("mecz_dru_id_gosc"));
				int kolejka = cursor.getInt(cursor.getColumnIndex("mecz_kolejka"));
				
				Cursor cursorGospodarz = baza.getNazwyDruzyn(idGospodarz);
				if(cursorGospodarz!=null && cursorGospodarz.moveToFirst())
					vHolder.tvGospodarz.setText(cursorGospodarz.getString(cursorGospodarz.getColumnIndex("dru_nazwa")));
				
				Cursor cursorGosc = baza.getNazwyDruzyn(idGosc);
				if(cursorGosc!=null && cursorGosc.moveToFirst())
					vHolder.tvGosc.setText(cursorGosc.getString(cursorGosc.getColumnIndex("dru_nazwa")));
				vHolder.tvKolejka.setText(String.valueOf(kolejka));

			
			
			return rowView;
		}
	}

}


class ZawodnikStatystykiCursorAdapter extends SimpleCursorAdapter
{

	private Activity context;
	private Cursor cursor;

	public ZawodnikStatystykiCursorAdapter(Activity context, Cursor c) {
		super(context, R.layout.lv_zawodnik_statystyka, c, new String[]{}, new int[]{});
		this.context = context;
		this.cursor = c;

	
	}
	
	class ViewHolder {
        public TextView tvMinuta, tvStatystyka;
        public LinearLayout llWiersz;
     
    }
	
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder vHolder;
		View rowView = convertView;
		if(rowView == null)
		{
			LayoutInflater layoutInflater = context.getLayoutInflater();
	        rowView =  layoutInflater.inflate(R.layout.lv_zawodnik_statystyka, null, true);
	        vHolder = new ViewHolder();

	        vHolder.tvMinuta = (TextView) rowView.findViewById(R.id.tvStatystykiZawodnikMinuta);
	        vHolder.tvStatystyka = (TextView) rowView.findViewById(R.id.tvStatystykiZawodnik);
	        vHolder.llWiersz = (LinearLayout) rowView.findViewById(R.id.llStatystykiZawodnikWiersz);
	        rowView.setTag(vHolder);
        } 
		
		
		else 
            vHolder = (ViewHolder) rowView.getTag();
        
		
		if(position%2 == 0)
			vHolder.llWiersz.setBackgroundColor(Color.parseColor("#3F3F3F"));
		else
			vHolder.llWiersz.setBackgroundColor(Color.parseColor("#232323"));
		
        cursor.moveToPosition(position);
        String nazwa = cursor.getString(cursor.getColumnIndex("sta_nazwa")), tekst = "";
        if(nazwa.equals("zolta"))
        {
        	tekst = "Otrzymuje ¿ó³t¹ kartkê";
        }
        else if(nazwa.equals("czerwona"))
        {
        	tekst = "Otrzymuje czerwon¹ kartkê";
        }
        else if(nazwa.equals("bramka"))
        {
        	tekst = "Strzela bramkê";
        }
        else
        {
        	tekst = nazwa;
        }
        
        vHolder.tvMinuta.setText(" " + String.valueOf(cursor.getInt(cursor.getColumnIndex("sta_minuta")))+"'"); 
        vHolder.tvStatystyka.setText(tekst);
        
		return rowView;
	}
	
}



