package com.example.zarzadcaklubowy;

import java.util.ArrayList;

import com.example.zarzadcaklubowy.MyCursorAdapter.ViewHolder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MeczStatystyki extends Activity implements OnClickListener{
	
	private ListView lvStatystyki;
	private Context context;
	private BazaDanych baza;
	private Cursor cursorStatystyki, pozycja;
	private long meczID;
	private Button btUsun;
	public Button getBtUsun() {
		return btUsun;
	}

	public void setBtUsun(Button btUsun) {
		this.btUsun = btUsun;
	}

	private StatystykiCursorAdapter dataAdapter;
	public static int czyKoniec = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mecz_statystyki);
		Przypisz();
		
		Bundle extras = getIntent().getExtras(); 
		if(extras !=null) {
		    meczID = extras.getLong("mecz");
		    PobierzStatystyki();
		}
		
		Cursor c = baza.getStatystykiMeczNazwa(meczID, "koniec");
		if(c!=null) czyKoniec = c.getCount();
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mecz_statystyki, menu);
		return true;
	}
	
	private void PobierzStatystyki()
	{
		String [] kolumny = new String[]{};
		int [] to = new int[]{};
		cursorStatystyki = baza.getStatystykiBezSkladu(meczID);
		dataAdapter = new StatystykiCursorAdapter(this,R.layout.lv_mecz_statystyki_layout,cursorStatystyki,kolumny,to, baza, btUsun);
		lvStatystyki.setAdapter(dataAdapter);
	}
	
	private void Przypisz()
	{
		lvStatystyki = (ListView) findViewById(R.id.lvMeczStatystyki);
		btUsun = (Button)findViewById(R.id.btMeczStatystykiUsun);
		btUsun.setOnClickListener(this);
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        if(dataAdapter.getBaza()!=null)
        {
        	dataAdapter.getBaza().close();
        }
        super.onDestroy();
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		if(czyKoniec>=1)
		{
			if (keyCode == KeyEvent.KEYCODE_BACK) {
		        Intent intent = new Intent(context, Mecz.class);
		        startActivity(intent);
		        return true;
		    }
		    return super.onKeyDown(keyCode, event);
		}
		
		else
		{
			if (keyCode == KeyEvent.KEYCODE_BACK) {
		        Intent intent = new Intent(context, MeczRozegraj.class);
		        intent.putExtra("mecz", meczID);
		        startActivity(intent);
		        return true;
		    }
		    return super.onKeyDown(keyCode, event);
		}
	    
	}

	@Override
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.btMeczStatystykiUsun:
			long UsunID;
			for (Integer i : StatystykiCursorAdapter.POZYCJE) {
				pozycja = (Cursor)lvStatystyki.getItemAtPosition(i);
				if(pozycja!=null && pozycja.moveToPosition(pozycja.getPosition()))
				{
					UsunID = pozycja.getLong(pozycja.getColumnIndex("_id"));
					baza.deleteStatystyka(UsunID);
					
					
				}
			}
			StatystykiCursorAdapter.POZYCJE.clear();
			if(pozycja!=null)
			pozycja.requery();
			PobierzStatystyki();
			
			break;
		}
		
	}
	

}

class StatystykiCursorAdapter extends SimpleCursorAdapter
{
	public static final ArrayList<Integer> POZYCJE = new ArrayList<Integer>();
	private Activity context;
	private Cursor cursor;
	private BazaDanych baza;
	public BazaDanych getBaza() {
		return baza;
	}


	public void setBaza(BazaDanych baza) {
		this.baza = baza;
	}


	private Button btUsun;
	public StatystykiCursorAdapter(Activity context, int layout, Cursor c,
			String[] from, int[] to, BazaDanych b, Button bt) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
		baza = b;
		btUsun = bt;
	
	}
	
	class ViewHolder {
        public TextView tvMinuta, tvStatystyka;
        public CheckBox chbox;
     
    }
	
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder vHolder;
		View rowView = convertView;
		if(rowView == null)
		{
			LayoutInflater layoutInflater = context.getLayoutInflater();
	        rowView =  layoutInflater.inflate(R.layout.lv_mecz_statystyki_layout, null, true);
	        vHolder = new ViewHolder();
	        vHolder.chbox = (CheckBox) rowView.findViewById(R.id.chbStatystyki);
	        vHolder.tvStatystyka = (TextView) rowView.findViewById(R.id.tvStatystyki);  
	        vHolder.tvMinuta = (TextView) rowView.findViewById(R.id.tvStatystykiMinuta);
	        rowView.setTag(vHolder);
        } 
		
		else 
            vHolder = (ViewHolder) rowView.getTag();
        
        cursor.moveToPosition(position);
        
        if(MeczStatystyki.czyKoniec>=1)
        {
        	vHolder.chbox.setVisibility(View.GONE);
        	btUsun.setVisibility(View.GONE);
        }
        else
        {
        	vHolder.chbox.setVisibility(View.VISIBLE);
        	btUsun.setVisibility(View.VISIBLE);
        }
        
        if(POZYCJE.contains(position))
        {
        	vHolder.chbox.setChecked(true);
        }
        else
        {
        	vHolder.chbox.setChecked(false);
        }
        
        vHolder.tvMinuta.setText(" " + String.valueOf(cursor.getInt(cursor.getColumnIndex("sta_minuta")))+"'");
        String nazwa = cursor.getString(cursor.getColumnIndex("sta_nazwa"));
        Cursor zawodnik = baza.getZawodnicy(cursor.getLong(cursor.getColumnIndex("sta_zaw_id")));
        String tekst = "";
        
        
        if(nazwa.equals("bramkaprzeciwnik"))
        {
        	tekst = "Przeciwnicy zdobywaj¹ bramkê";
        }
        
        else if(nazwa.equals("koniec"))
        {
        	tekst = "Koniec meczu";
        }
        else
        {
        	tekst = nazwa;
        }
        if(zawodnik!=null && zawodnik.moveToFirst())
        {
        	String imie = zawodnik.getString(zawodnik.getColumnIndex("zaw_imie"));
        	String nazwisko = zawodnik.getString(zawodnik.getColumnIndex("zaw_nazwisko"));
	        if(nazwa.equals("zolta"))
	        {
	        	tekst = imie + " " + nazwisko + " otrzymuje ¿ó³t¹ kartkê";
	        }
	        else if(nazwa.equals("czerwona"))
	        {
	        	tekst = imie + " " + nazwisko + " otrzymuje czerwon¹ kartkê";
	        }
	        else if(nazwa.equals("bramka"))
	        {
	        	tekst = imie + " " + nazwisko + " zdobywa bramkê";
	        }
	        else
	        {
	        	tekst = nazwa;
	        }
        }
        
        
	        
	     vHolder.tvStatystyka.setText(tekst);
        
        
        
        
        /*
        vHolder.tv.setText(cursor.getString(cursor.getColumnIndex("dru_nazwa")));*/
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






