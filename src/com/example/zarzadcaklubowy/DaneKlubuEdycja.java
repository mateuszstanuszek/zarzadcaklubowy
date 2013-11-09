package com.example.zarzadcaklubowy;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DaneKlubuEdycja extends Activity implements OnClickListener{

	private EditText etNazwa,etRok,etTelefon,etAdres;
	private Button btZatwierdz;
	private BazaDanych baza;
	private Context context;
	private Cursor cursor,cursorDruzyna;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dane_klubu_edycja);
		etNazwa = (EditText)findViewById(R.id.etNazwaKlEd);
		etRok = (EditText)findViewById(R.id.etRokEd);
		etTelefon = (EditText)findViewById(R.id.etTelefonEd);
		etAdres = (EditText)findViewById(R.id.etAdresEd);
		btZatwierdz = (Button)findViewById(R.id.btZatwierdzEd);
		btZatwierdz.setOnClickListener(this);
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
		cursor = baza.getDaneKlubu();
		if(cursor!=null && cursor.moveToFirst())
		{
			long id = cursor.getLong(cursor.getColumnIndex("dan_dru_id"));
			cursorDruzyna = baza.getNazwyDruzyn(id);
				if(cursorDruzyna!=null && cursorDruzyna.moveToFirst())
					etNazwa.setText(cursorDruzyna.getString(cursorDruzyna.getColumnIndex("dru_nazwa")));
				
			etAdres.setText(cursor.getString(cursor.getColumnIndex("dan_adres")));
			etRok.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dan_rok"))));
			etTelefon.setText(String.valueOf(cursor.getInt(cursor.getColumnIndex("dan_telefon"))));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dane_klubu_edycja, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btZatwierdzEd:
			if(cursor!=null && cursor.moveToFirst())
			{
				long id = cursor.getLong(cursor.getColumnIndex("_id"));
				baza.updateDaneKlubu(id, Integer.parseInt(etRok.getText().toString()),
						etAdres.getText().toString(), Integer.parseInt(etTelefon.getText().toString()));
				if(cursorDruzyna!=null && cursorDruzyna.moveToFirst())
				{
					long idDruzyna = cursor.getLong(cursor.getColumnIndex("dan_dru_id"));
					baza.updateNazwaDruzyny(idDruzyna, etNazwa.getText().toString());
				}
				
			}
			
			Intent intent = new Intent(context, DaneKlubu.class);
			startActivity(intent);
			break;
		}
		// TODO Auto-generated method stub
		
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }

}
