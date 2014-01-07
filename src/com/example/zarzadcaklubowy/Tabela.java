package com.example.zarzadcaklubowy;

import java.util.ArrayList;

import com.example.zarzadcaklubowy.MyCursorAdapter.ViewHolder;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class Tabela extends Activity {

	private ListView lvTabela;
	private TabelaCursorAdapter dataAdapter;
	private BazaDanych baza;
	private Cursor cursor;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabela);
		lvTabela = (ListView)findViewById(R.id.lv_Tabela);
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		cursor = baza.getDruzynyDoTabeli();
		if(cursor!=null && cursor.moveToFirst())
		{
			String [] kolumny = new String[]{"dru_nazwa","dru_mecze","dru_punkty","dru_zwyciestwa",
					"dru_remisy","dru_porazki","dru_bramki_zdobyte","dru_bramki_stracone"};
			int [] to = new int[]{R.id.tvNazwaDruzyny,R.id.tvTabelaMecze, R.id.tvTabelaPunkty,R.id.tvTabelaZwyciestwa,
					R.id.tvTabelaRemisy,R.id.tvTabelaPorazki,R.id.tvTabelaBramkiZdobyte,R.id.tvTabelaBramkiStracone};
			dataAdapter = new TabelaCursorAdapter(this, R.layout.lv_tabela_layout, cursor, kolumny, to);
			lvTabela.setAdapter(dataAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tabela, menu);
		return true;
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }

}

class TabelaCursorAdapter extends SimpleCursorAdapter
{
	private Activity context;
	private Cursor cursor;
	public TabelaCursorAdapter(Activity context, int layout, Cursor c,
			String[] from, int[] to) {
		super(context, layout, c, from, to);
		this.context = context;
		this.cursor = c;
		// TODO Auto-generated constructor stub
	}
	
	static class ViewHolder {
        public TextView tvMiejsce, tvDruzyna, tvMecze, tvPunkty, tvZwyciestwa, tvRemisy, tvPorazki, tvBramikZdobyte, tvBramkiStracone;
     
    }
	
	
	@Override
    public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolder vHolder;
		View rowView = convertView;
		if(rowView == null)
		{
			LayoutInflater layoutInflater = context.getLayoutInflater();
	        rowView =  layoutInflater.inflate(R.layout.lv_tabela_layout, null, true);
	        vHolder = new ViewHolder();
	        vHolder.tvMiejsce = (TextView)rowView.findViewById(R.id.tvTabelaMiejsce);
	        vHolder.tvDruzyna = (TextView)rowView.findViewById(R.id.tvTabelaDruzyna);
	        vHolder.tvMecze = (TextView)rowView.findViewById(R.id.tvTabelaMecze);
	        vHolder.tvPunkty = (TextView)rowView.findViewById(R.id.tvTabelaPunkty);
	        vHolder.tvZwyciestwa = (TextView)rowView.findViewById(R.id.tvTabelaZwyciestwa);
	        vHolder.tvRemisy = (TextView)rowView.findViewById(R.id.tvTabelaRemisy);
	        vHolder.tvPorazki = (TextView)rowView.findViewById(R.id.tvTabelaPorazki);
	        vHolder.tvBramikZdobyte = (TextView)rowView.findViewById(R.id.tvTabelaBramkiZdobyte);
	        vHolder.tvBramkiStracone = (TextView)rowView.findViewById(R.id.tvTabelaBramkiStracone);
	        rowView.setTag(vHolder);
		}
		else 
            vHolder = (ViewHolder) rowView.getTag();
		
			cursor.moveToPosition(position);
		
			vHolder.tvMiejsce.setText(String.valueOf(position+1));
	        vHolder.tvDruzyna.setText(cursor.getString(cursor.getColumnIndex("dru_nazwa")));
	        vHolder.tvMecze.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_mecze"))));
	        vHolder.tvPunkty.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_punkty"))));
	        vHolder.tvZwyciestwa.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_zwyciestwa"))));
	        vHolder.tvRemisy.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_remisy"))));
	        vHolder.tvPorazki.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_porazki"))));
	        vHolder.tvBramikZdobyte.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_bramki_zdobyte"))));
	        vHolder.tvBramkiStracone.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dru_bramki_stracone"))));
		
		
		return rowView;
	}
}
