package com.example.zarzadcaklubowy;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Druzyny extends Activity implements OnClickListener{
	
	private ListView lvDruzyny;
	private MyCursorAdapter dataAdapter;
	private BazaDanych baza;
	private Cursor cursor;
	private Cursor cursorAktualnySezon;
	private EditText etNazwaDruzyny;
	private Button btNowaDruzyna, btAnulujDruzyna, btZapiszDruzyna, btUsunDruzyna;
	private LinearLayout llStandarPanel, llNoweZadaniePanel;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_druzyny);
		lvDruzyny = (ListView)findViewById(R.id.lvDruzyny);
		etNazwaDruzyny = (EditText)findViewById(R.id.etNazwa_Druzyna);
		btNowaDruzyna = (Button)findViewById(R.id.btDodaj_Druzyna);
		btAnulujDruzyna = (Button)findViewById(R.id.btAnuluj_Druzyna);
		btZapiszDruzyna = (Button)findViewById(R.id.btZapisz_Druzyna);
		btUsunDruzyna = (Button)findViewById(R.id.btUsun_Druzyna);
		llStandarPanel = (LinearLayout)findViewById(R.id.linearLayout_Widoczne);
		llNoweZadaniePanel = (LinearLayout)findViewById(R.id.linearLayout_NowaDruzyna);
		
	
		
		btNowaDruzyna.setOnClickListener(this);
		btAnulujDruzyna.setOnClickListener(this);
		btZapiszDruzyna.setOnClickListener(this);
		btUsunDruzyna.setOnClickListener(this);
		
		baza = new BazaDanych(getApplicationContext());
		baza.open();
		
		PobierzDruzyny();
		
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.druzyny, menu);
		return true;
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }
	
	public void PobierzDruzyny()
	{
		String [] kolumny = new String[]{"dru_nazwa"};
		int [] to = new int[]{R.id.tvNazwaDruzyny};
		cursor = baza.getNazwyDruzyn();
		dataAdapter = new MyCursorAdapter(this,R.layout.lv_druzyny_layout,cursor,kolumny,to);
		lvDruzyny.setAdapter(dataAdapter);
		
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btDodaj_Druzyna:
			pokazNoweZadaniePanel();
			break;
		case R.id.btZapisz_Druzyna:
			if (etNazwaDruzyny.getText().toString().equalsIgnoreCase(""))
			{
				etNazwaDruzyny.setError("Nazwa nie mo¿e byæ pusta!");
			}
			else
			{
				etNazwaDruzyny.setError(null);
				cursorAktualnySezon = baza.getSezonAktualny();
				if(cursorAktualnySezon!=null && cursorAktualnySezon.moveToFirst())
					baza.insertDruzyna(cursorAktualnySezon.getInt(cursorAktualnySezon.getColumnIndex("_id")),etNazwaDruzyny.getText().toString());
				else
				{
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			        alertDialog.setTitle("Powiadomienie");
			        alertDialog.setMessage("Wybierz aktualny sezon!");
			        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog,int which) {
			            	
			            	dialog.cancel();
			            	
			            }
			        });
			        
			        alertDialog.show();
				}
				etNazwaDruzyny.setText("");
				pokazStandardowyPanel();
				schowajKlawiature();
				cursor.requery();
			}
			
			break;
		case R.id.btAnuluj_Druzyna:
			etNazwaDruzyny.setError(null);
			etNazwaDruzyny.setText("");
			pokazStandardowyPanel();
			schowajKlawiature();
			break;
		case R.id.btUsun_Druzyna:

			for (Integer i : MyCursorAdapter.POZYCJE) {
				cursor = (Cursor)lvDruzyny.getItemAtPosition(i);
				if(cursor!=null)
				{
					long UsunID = cursor.getLong(cursor.getColumnIndex("_id"));
					Cursor c;
					c = baza.getMeczZDruzyna(UsunID);
					Cursor mojaDruzyna = baza.getDaneKlubu();
					long DruzynaID = -1;
					if(mojaDruzyna!=null && mojaDruzyna.moveToFirst())
						DruzynaID = mojaDruzyna.getLong(mojaDruzyna.getColumnIndex("dan_dru_id"));

					if (c!=null && !c.moveToFirst() && DruzynaID!=UsunID)
						baza.deleteDruzyny(UsunID);
					else if(DruzynaID==UsunID)
					{
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				        alertDialog.setTitle("Powiadomienie");
				        alertDialog.setMessage("Nie mo¿esz usun¹æ w³aœnej dru¿yny.");
				        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog,int which) {
				            	
				            	dialog.cancel();
				            	
				            }
				        });
				        
				        alertDialog.show();
					}
					else
					{
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
				        alertDialog.setTitle("Powiadomienie");
				        alertDialog.setMessage("Wybrana dru¿yna uczestniczy w rozgrywkach aktualnego sezonu. Je¿eli chcesz j¹ usun¹æ musisz anulowaæ jej mecze w terminarzu!");
				        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog,int which) {
				            	
				            	dialog.cancel();
				            	
				            }
				        });
				        
				        alertDialog.show();
					}
				}
				
				
				
			}
			MyCursorAdapter.POZYCJE.clear();
			if(cursor!=null)
			cursor.requery();
			PobierzDruzyny();
			pokazStandardowyPanel();
			break;
		};
		
	}
	
	private void pokazNoweZadaniePanel()
	{
		setVisibilityOf(etNazwaDruzyny, true);
		setVisibilityOf(llNoweZadaniePanel, true);
		setVisibilityOf(llStandarPanel, false);
		
	}
	private void pokazStandardowyPanel()
	{
		setVisibilityOf(etNazwaDruzyny, false);
		setVisibilityOf(llNoweZadaniePanel, false);
		setVisibilityOf(llStandarPanel, true);
	}
	
	private void setVisibilityOf(View v, boolean visible) {
	    int visibility = visible ? View.VISIBLE : View.GONE;
	    v.setVisibility(visibility);
	}
	
	private void schowajKlawiature() {
	    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(etNazwaDruzyny.getWindowToken(), 0);
	}

}
//----------------------------
class MyCursorAdapter extends SimpleCursorAdapter
{
	public static final ArrayList<Integer> POZYCJE = new ArrayList<Integer>();
	private Activity context;
	private Cursor cursor;
	public MyCursorAdapter(Activity context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
	
	}
	
	static class ViewHolder {
        public TextView tv;
        public CheckBox chbox;
     
    }
	
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder vHolder;
		View rowView = convertView;
		if(rowView == null)
		{
			LayoutInflater layoutInflater = context.getLayoutInflater();
	        rowView =  layoutInflater.inflate(R.layout.lv_druzyny_layout, null, true);
	        vHolder = new ViewHolder();
	        vHolder.chbox = (CheckBox) rowView.findViewById(R.id.checkBox1);
	        vHolder.tv = (TextView) rowView.findViewById(R.id.tvNazwaDruzyny);    
	        rowView.setTag(vHolder);
        } 
		
		else 
            vHolder = (ViewHolder) rowView.getTag();
        
        cursor.moveToPosition(position);
        
        if(POZYCJE.contains(position))
        {
        	vHolder.chbox.setChecked(true);
        }
        else
        {
        	vHolder.chbox.setChecked(false);
        }
        
        vHolder.tv.setText(cursor.getString(cursor.getColumnIndex("dru_nazwa")));
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
