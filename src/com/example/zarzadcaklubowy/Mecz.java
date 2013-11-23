package com.example.zarzadcaklubowy;

import java.util.ArrayList;
import java.util.List;

import com.example.zarzadcaklubowy.MyCursorAdapter.ViewHolder;

import android.opengl.Visibility;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class Mecz extends Activity implements OnItemClickListener, OnClickListener{
	
	
	
	private ListView lvZawodnicy, lvPodstawowi, lvRezerwowi;
	private TextView tvMeczKolejka, tvMeczGospodarz, tvMeczGosc, tvMeczData;
	private LinearLayout llZawodnicy;
	private Button btMeczPoprzedni, btMeczNastepny, btMeczRozegraj, btMeczKontunuuj, btMeczStatystyki;
	private Context context;
	private BazaDanych baza;
	private Cursor cursorZawodnicy, cursorMojaDruzyna, cursorMecze,cursorGospodarz,cursorGosc, cursorStatystyki;
	private MeczArrayAdapter zawodnicyAdapter, podstawowiAdapter, rezerwowiAdapter;
	private ArrayList<ZawodnikMecz> zawodnicy, podstawowi, rezerwowi;
	private long mojaDruzynaID,gospodarzID, goscID, meczID;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mecz);
		
		Przypisz();
		
		cursorZawodnicy = baza.getZawodnicy();
		if(cursorZawodnicy!=null && cursorZawodnicy.moveToFirst())
		{
			do 
			{
				ZawodnikMecz zm = new ZawodnikMecz();
				zm.setId(cursorZawodnicy.getInt(cursorZawodnicy.getColumnIndex("_id")));
				zm.setImie(cursorZawodnicy.getString(cursorZawodnicy.getColumnIndex("zaw_imie")));
				zm.setNazwisko(cursorZawodnicy.getString(cursorZawodnicy.getColumnIndex("zaw_nazwisko")));
				zawodnicy.add(zm);
				
			}
			while (!cursorZawodnicy.isLast() && cursorZawodnicy.moveToNext());
		}
		
		zawodnicyAdapter = new MeczArrayAdapter(this, R.layout.lv_mecz_zawodnicy_layout, zawodnicy);
		lvZawodnicy.setAdapter(zawodnicyAdapter);
		lvZawodnicy.setOnItemClickListener(this);
		
		podstawowiAdapter = new MeczArrayAdapter(this, R.layout.lv_mecz_zawodnicy_layout, podstawowi);
		lvPodstawowi.setAdapter(podstawowiAdapter);
		lvPodstawowi.setOnItemClickListener(this);
		
		rezerwowiAdapter = new MeczArrayAdapter(this, R.layout.lv_mecz_zawodnicy_layout, rezerwowi);
		lvRezerwowi.setAdapter(rezerwowiAdapter);
		lvRezerwowi.setOnItemClickListener(this);
		
		
		cursorMojaDruzyna = baza.getDaneKlubu();
		if(cursorMojaDruzyna!=null && cursorMojaDruzyna.moveToFirst())
		{
			mojaDruzynaID = cursorMojaDruzyna.getLong(cursorMojaDruzyna.getColumnIndex("dan_dru_id"));
			cursorMecze = baza.getMeczZDruzyna(mojaDruzynaID);
			if(cursorMecze!=null && cursorMecze.moveToFirst())
			{
				SprawdzStatystyki();
				WprowadzText();
				
			}
			else
			{
				llZawodnicy.setVisibility(View.GONE);
				btMeczKontunuuj.setVisibility(View.GONE);
				btMeczRozegraj.setVisibility(View.GONE);
				btMeczStatystyki.setVisibility(View.GONE);
				tvMeczKolejka.setText("-");
				tvMeczGospodarz.setText("---");
				tvMeczGosc.setText("---");
				tvMeczData.setText("");
				
			}
		}
		
		//baza.deleteWykluczenia();
	}
	
	
	
	private void Przypisz()
	{
		lvZawodnicy = (ListView)findViewById(R.id.lvMeczZawodnicy);
		lvPodstawowi = (ListView)findViewById(R.id.lvMeczPodstawowi);
		lvRezerwowi = (ListView)findViewById(R.id.lvMeczRezerwowi);
		tvMeczData = (TextView)findViewById(R.id.tvMeczData);
		tvMeczKolejka= (TextView)findViewById(R.id.tvMeczKolejka);
		tvMeczGosc = (TextView)findViewById(R.id.tvMeczGosc);
		tvMeczGospodarz = (TextView)findViewById(R.id.tvMeczGospodarz);
		btMeczNastepny = (Button)findViewById(R.id.btMeczNastepny);
		btMeczPoprzedni = (Button)findViewById(R.id.btMeczPoprzedni);
		btMeczRozegraj = (Button)findViewById(R.id.btMeczRozegraj);
		btMeczKontunuuj = (Button)findViewById(R.id.btMeczKontynuuj);
		btMeczStatystyki = (Button)findViewById(R.id.btMeczStatystyki);
		
		llZawodnicy = (LinearLayout)findViewById(R.id.llMeczZawodnicy);
		btMeczNastepny.setOnClickListener(this);
		btMeczPoprzedni.setOnClickListener(this);
		btMeczRozegraj.setOnClickListener(this);
		btMeczStatystyki.setOnClickListener(this);
		btMeczKontunuuj.setOnClickListener(this);
		
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		zawodnicy = new ArrayList<ZawodnikMecz>();
		podstawowi = new ArrayList<ZawodnikMecz>();
		rezerwowi = new ArrayList<ZawodnikMecz>();
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mecz, menu);
		return true;
	}
	
	@Override
	protected void onResume()
	{
		if(cursorMecze!=null && cursorMecze.moveToPosition(cursorMecze.getPosition()))
		{
			SprawdzStatystyki();
		}
		else
		{
			llZawodnicy.setVisibility(View.GONE);
			btMeczKontunuuj.setVisibility(View.GONE);
			btMeczRozegraj.setVisibility(View.GONE);
			btMeczStatystyki.setVisibility(View.GONE);
			tvMeczKolejka.setText("-");
			tvMeczGospodarz.setText("---");
			tvMeczGosc.setText("---");
			tvMeczData.setText("");
			
		}
		super.onResume();
		
	}
	
	@Override
    protected void onDestroy() {
		
		if(cursorGosc!=null)
			cursorGosc.close();
		
		if(cursorZawodnicy!=null)
			cursorZawodnicy.close();
		
		if(cursorMojaDruzyna!=null)
			cursorMojaDruzyna.close();
		
		if(cursorMecze!=null)
			cursorMecze.close();
		
		if(cursorStatystyki!=null)
			cursorStatystyki.close();
		
        if(baza != null)
            baza.close();
        super.onDestroy();
    }

	@Override
	public void onItemClick(AdapterView<?> av, View view, int position, long id) {
		ZawodnikMecz z;
		switch(av.getId())
		{
		case R.id.lvMeczZawodnicy:
			z = zawodnicy.get(position);
			if(!z.isPauza())
			{
				if(podstawowi.size()<11)
				{
					
					zawodnicyAdapter.remove(z);
					podstawowiAdapter.insert(z, podstawowiAdapter.getCount());
				}
				
				else if (rezerwowi.size()<7)
				{
					
					zawodnicyAdapter.remove(z);
					rezerwowiAdapter.insert(z, rezerwowiAdapter.getCount());
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Pe³na kadra", Toast.LENGTH_SHORT).show();
				}
			}
			
			zawodnicyAdapter.notifyDataSetChanged();
			break;
			
		case R.id.lvMeczPodstawowi:
			z = podstawowi.get(position);
			podstawowiAdapter.remove(z);
			zawodnicyAdapter.insert(z, zawodnicyAdapter.getCount());
			//Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
			break;
		case R.id.lvMeczRezerwowi:
			z = rezerwowi.get(position);
			if(podstawowi.size()<11)
			{
				rezerwowiAdapter.remove(z);
				podstawowiAdapter.insert(z, podstawowiAdapter.getCount());
			}
			else
			{
				rezerwowiAdapter.remove(z);
				zawodnicyAdapter.insert(z, zawodnicyAdapter.getCount());
			}
			//Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
			break;
			
		}
		
		
		
		
	}
	


	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btMeczPoprzedni:
			
			if(cursorMecze!=null)
			{
				if(cursorMecze.moveToPrevious())
				{
					SprawdzStatystyki();
					WprowadzText();
				}
				else cursorMecze.moveToPosition(cursorMecze.getPosition()+1);
						
			}
			break;
		case R.id.btMeczNastepny:

			if(cursorMecze!=null)
			{
				if (cursorMecze.moveToNext())
				{
					SprawdzStatystyki();
					WprowadzText();
				}
				else cursorMecze.moveToPosition(cursorMecze.getPosition()-1);
			}
			break;
		case R.id.btMeczRozegraj:
			if(cursorMecze!=null && cursorMecze.moveToPosition(cursorMecze.getPosition()))
			{
				long meczID = cursorMecze.getLong(cursorMecze.getColumnIndex("_id"));
				if(!podstawowi.isEmpty())
				{
					for (ZawodnikMecz zawodnik : podstawowi) {
						baza.insertStatystyka(meczID, zawodnik.getId(), "podstawowy", 0);
						baza.insertStatystyka(meczID, zawodnik.getId(), "wejscie", 1);
					}
					
					for (ZawodnikMecz zawodnik : rezerwowi) {
						baza.insertStatystyka(meczID, zawodnik.getId(), "rezerwowy", 0);
					}
					
					Intent intent = new Intent(context, MeczRozegraj.class);
					intent.putExtra("mecz", meczID);
					startActivity(intent);
				}
				else
					Toast.makeText(getApplicationContext(), "Nie wybra³eœ ¿adnego zawodnika", Toast.LENGTH_SHORT).show();
					
			}
			
			
			
			break;
		case R.id.btMeczKontynuuj:
			if(cursorMecze!=null && cursorMecze.moveToPosition(cursorMecze.getPosition()))
			{
				long meczID = cursorMecze.getLong(cursorMecze.getColumnIndex("_id"));
				Intent intent = new Intent(context, MeczRozegraj.class);
				intent.putExtra("mecz", meczID);
				startActivity(intent);
			}
			break;
		case R.id.btMeczStatystyki:
			long meczID = cursorMecze.getLong(cursorMecze.getColumnIndex("_id"));
			Intent intent = new Intent(context, MeczStatystyki.class);
			intent.putExtra("mecz", meczID);
			startActivity(intent);
			break;
		}
		
	}
	
	private void SprawdzStatystyki()
	{
		meczID = cursorMecze.getLong(cursorMecze.getColumnIndex("_id"));
		cursorStatystyki = baza.getStatystykiMeczu(meczID);
		if(cursorStatystyki!=null && cursorStatystyki.moveToFirst())
		{
			llZawodnicy.setVisibility(View.GONE);
			Cursor c = baza.getStatystykiMeczNazwa(meczID, "koniec");
			if(c!=null && c.moveToFirst())
			{
				btMeczKontunuuj.setVisibility(View.GONE);
				btMeczRozegraj.setVisibility(View.GONE);
				btMeczStatystyki.setVisibility(View.VISIBLE);
			}
			else
			{
				btMeczKontunuuj.setVisibility(View.VISIBLE);
				btMeczRozegraj.setVisibility(View.GONE);
				btMeczStatystyki.setVisibility(View.GONE);
			}
			
		}
		else
		{
			llZawodnicy.setVisibility(View.VISIBLE);
			btMeczKontunuuj.setVisibility(View.GONE);
			btMeczRozegraj.setVisibility(View.VISIBLE);
			btMeczStatystyki.setVisibility(View.GONE);
			
		}
	}
	
	private void WprowadzText()
	{
		
			tvMeczKolejka.setText(String.valueOf(cursorMecze.getInt(cursorMecze.getColumnIndex("mecz_kolejka"))));
			
			gospodarzID = cursorMecze.getLong(cursorMecze.getColumnIndex("mecz_dru_id_gospodarz"));
			goscID = cursorMecze.getLong(cursorMecze.getColumnIndex("mecz_dru_id_gosc"));
			cursorGospodarz = baza.getNazwyDruzyn(gospodarzID);
			cursorGosc = baza.getNazwyDruzyn(goscID);
			if(cursorGospodarz!=null && cursorGospodarz.moveToFirst())
				tvMeczGospodarz.setText(cursorGospodarz.getString(cursorGospodarz.getColumnIndex("dru_nazwa")));
			if(cursorGosc!=null && cursorGosc.moveToFirst())
				tvMeczGosc.setText(cursorGosc.getString(cursorGosc.getColumnIndex("dru_nazwa")));
			
			tvMeczData.setText(cursorMecze.getString(cursorMecze.getColumnIndex("mecz_data")));
		
	}
	
	
	class MeczArrayAdapter extends ArrayAdapter<ZawodnikMecz>
	{
		private Activity context;
		public MeczArrayAdapter(Activity context, int layout, List<ZawodnikMecz> lista) {
			super(context, layout,lista);
			this.context = context;
		
		}
		
		class ViewHolder {
	        public TextView tvImie, tvNazwisko;
	     
	    }
		
		
		@Override
	    public View getView(final int position, View convertView, ViewGroup parent) {
			
			ViewHolder vHolder;
			View rowView = convertView;
			ZawodnikMecz zawodnik = getItem(position);
			Cursor cursorWykluczenie = baza.getWykluczeniaZawodnikAktywne(zawodnik.getId());
			if(cursorWykluczenie!=null && cursorWykluczenie.moveToFirst())
				zawodnik.setPauza(true);
			if(rowView == null)
			{
				LayoutInflater layoutInflater = context.getLayoutInflater();
		        rowView =  layoutInflater.inflate(R.layout.lv_mecz_zawodnicy_layout, null, true);
		        vHolder = new ViewHolder();
		        vHolder.tvImie = (TextView) rowView.findViewById(R.id.tvMeczImie);
		        vHolder.tvNazwisko = (TextView) rowView.findViewById(R.id.tvMeczNazwisko);    
		        rowView.setTag(vHolder);
	        } 
			
			else 
	            vHolder = (ViewHolder) rowView.getTag();

			
			if(zawodnik.isPauza())
			{
				vHolder.tvImie.setText(zawodnik.getImie());
				vHolder.tvNazwisko.setText(zawodnik.getNazwisko());
				vHolder.tvImie.setTextColor(Color.RED);
				vHolder.tvNazwisko.setTextColor(Color.RED);
				vHolder.tvImie.setPaintFlags(vHolder.tvImie.getPaintFlags() |
                        Paint.STRIKE_THRU_TEXT_FLAG);
				vHolder.tvNazwisko.setPaintFlags(vHolder.tvNazwisko.getPaintFlags() |
                        Paint.STRIKE_THRU_TEXT_FLAG);
				
				
			}
			else
			{
				vHolder.tvImie.setText(zawodnik.getImie());
				vHolder.tvNazwisko.setText(zawodnik.getNazwisko());
				vHolder.tvImie.setTextColor(Color.WHITE);
				vHolder.tvNazwisko.setTextColor(Color.WHITE);
				vHolder.tvImie.setPaintFlags(vHolder.tvNazwisko.getPaintFlags() &
                        ~Paint.STRIKE_THRU_TEXT_FLAG);
				vHolder.tvNazwisko.setPaintFlags(vHolder.tvNazwisko.getPaintFlags() &
                        ~Paint.STRIKE_THRU_TEXT_FLAG);
			}
	        
			return rowView;
		}
		
		public boolean isEnabled(int position) {
			return !getItem(position).isPauza();
		}
		
		
		
	}


}







class ZawodnikMecz
{
	private long id;
	private String Imie;
	private String Nazwisko;
	private boolean pauza = false;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getImie() {
		return Imie;
	}
	public void setImie(String imie) {
		Imie = imie;
	}
	public String getNazwisko() {
		return Nazwisko;
	}
	public void setNazwisko(String nazwisko) {
		Nazwisko = nazwisko;
	}
	public boolean isPauza() {
		return pauza;
	}
	public void setPauza(boolean pauza) {
		this.pauza = pauza;
	}
	

}

