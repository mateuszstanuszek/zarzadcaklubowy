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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

public class DaneKlubuWprowadz extends Activity implements OnClickListener{
	
	private EditText etRok,etTelefon,etAdres,etNazwa;
	private Button btZatwierdz;
	private BazaDanych baza;
	private Context context;
	private Cursor cursor,cursorSezon,cursorDruzyna;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dane_klubu_wprowadz);
		etRok = (EditText)findViewById(R.id.etRok);
		etTelefon = (EditText)findViewById(R.id.etTelefon);
		etAdres = (EditText)findViewById(R.id.etAdres);
		etNazwa = (EditText)findViewById(R.id.etNazwa);
		btZatwierdz = (Button)findViewById(R.id.btZatwierdz);
		btZatwierdz.setOnClickListener(this);
		context = getApplicationContext();
		baza = new BazaDanych(context);
		baza.open();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dane_klubu_wprowadz, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.btZatwierdz:
			cursorSezon = baza.getSezonAktualny();
			if(cursorSezon!=null && cursorSezon.moveToFirst())
			{
				long id = baza.insertDruzyna(cursorSezon.getLong(cursorSezon.getColumnIndex("_id")), etNazwa.getText().toString());
				if(id>=0)
				baza.insertDaneKlubu(cursorSezon.getLong(cursorSezon.getColumnIndex("_id")), id, 
						Integer.parseInt(etRok.getText().toString()),
						etAdres.getText().toString(), Integer.parseInt(etTelefon.getText().toString()));
				Intent intent = new Intent(context, DaneKlubu.class);
				startActivity(intent);
			}

			
			break;
		}
		
	}
	
	@Override
    protected void onDestroy() {
        if(baza != null)
            baza.close();
        super.onDestroy();
    }

}
