package cat.kikevite.gasolina;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button b0, b1, b2, b3;
    EditText et1, et3;
    TextView tv1, tv2, tv4;
    Spinner sp;
    String sPosar, sPreu, sDescompte, sFalta;
    float dPosar, dPreu, dDescompte, dFalta;
    float[] preus = {0, 0, 0, 0};
    String url = "https://geoportalgasolineras.es/rest/2465/busquedaEstacionPrecio";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        b0 = findViewById(R.id.button0);
        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        et1 = findViewById(R.id.etPagar);
        et3 = findViewById(R.id.etDescompte);
        tv1 = findViewById(R.id.idMarcar);
        tv2 = findViewById(R.id.idPreuAmbDescompte);
        tv4 = findViewById(R.id.idFalta);
        sp = findViewById(R.id.spinnerPreu);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.preus, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);

        colors(0);
        connect();

        b0.setOnClickListener(view -> {
            colors(0);
            dPreu = preus[0];
            sp.setSelection(adapter.getPosition("" + preus[0]));
            //calcula();
        });

        b1.setOnClickListener(view -> {
            colors(1);
            dPreu = preus[1];
            sp.setSelection(adapter.getPosition("" + preus[1]));
            //calcula();
        });

        b2.setOnClickListener(view -> {
            colors(2);
            dPreu = preus[2];
            sp.setSelection(adapter.getPosition("" + preus[2]));
            //calcula();
        });

        b3.setOnClickListener(view -> {
            colors(3);
            dPreu = preus[3];
            sp.setSelection(adapter.getPosition("" + preus[3]));
            //calcula();
        });

        // POSAR
        et1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calcula();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // DESCOMPTE
        et3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                calcula();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void colors(int id) {
        b0.setBackgroundColor(Color.RED);
        b1.setBackgroundColor(Color.RED);
        b2.setBackgroundColor(Color.RED);
        b3.setBackgroundColor(Color.RED);
        switch (id) {
            case 0:
                b0.setBackgroundColor(Color.GREEN);
                break;
            case 1:
                b1.setBackgroundColor(Color.GREEN);
                break;
            case 2:
                b2.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                b3.setBackgroundColor(Color.GREEN);
                break;
            default:
                break;
        }
    }

    public void connect() {
        WebReaderTask wrTask = new WebReaderTask();
        wrTask.execute(url);
    }

    private class WebReaderTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            return WebReader.getURL(url[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            if (!s.contains("Error")) {
                int i = 0;
                String[] busca = {"precioGasoleoA","precioGasoleoPremium", "precioGasolina95E5", "precioGasolina98E5"};
                while (i < 4) {
                    int pos = s.indexOf(busca[i]) + busca[i].length() + 1;
                    preus[i] = Float.valueOf(s.substring(pos, pos + 5));
                    i++;
                }
                b0.setText("normal - " + preus[0]);
                b1.setText("e+10 - " + preus[1]);
                b2.setText("95 - " + preus[2]);
                b3.setText("98 - " + preus[3]);

                int posarA = (int) ((preus[0] * 100.0) - 100.9);
                sp.setSelection(posarA);
            } else {
                Toast.makeText(MainActivity.this, "Error al carregar dades", Toast.LENGTH_SHORT).show();
                b0.setText("normal");
                b1.setText("e+10");
                b2.setText("95");
                b3.setText("98");
            }
        }
    }

    void calcula() {
        //Log.i("kike", "posar: " +dPosar+ " - preu: " +dPreu+ " - descompte: " +dDescompte);
        sPosar = et1.getText().toString();
        sPosar = sPosar.isEmpty() ? "0.0" : sPosar;
        dPosar = Float.valueOf(sPosar);

        sDescompte = et3.getText().toString();
        sDescompte = sDescompte.isEmpty() ? "0.0" : sDescompte;
        dDescompte = Float.valueOf(sDescompte);
        float result = (dPosar / (dPreu - dDescompte)) * dPreu;
        //Log.i("kike", "marcar: " + result);
        tv1.setText("" + result);

        float preuDesc = dPosar - ((dPosar / dPreu) * dDescompte);
        tv2.setText("" + preuDesc);

        dFalta = (result - dPosar);

        tv4.setText("" + dFalta);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String spinnerPreu = sp.getSelectedItem().toString();
        sPreu = spinnerPreu.isEmpty() ? "0.0" : spinnerPreu;
        dPreu = Float.valueOf(sPreu);
        calcula();
        //colors(4);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}