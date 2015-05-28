package com.jonathanmackenzie.scrabblehelper;

import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String WORDLISTKEY = "words_list";
	private static final String TAG = "ScrabbleHelper";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		final EditText tv = (EditText) findViewById(R.id.editText1);
		final ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar1);
		final TextView scoreText = (TextView) findViewById(R.id.textView1);
		final ImageView ivCorrect = (ImageView) findViewById(R.id.imageViewCorrect);
		final ImageView ivMissing = (ImageView) findViewById(R.id.imageViewMissing);
		final Button clearButton = (Button) findViewById(R.id.button_clear);
		clearButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tv.setText("");
			}
		});
		clearButton.setVisibility(View.GONE);
		tv.setVisibility(View.INVISIBLE);
		scoreText.setVisibility(View.INVISIBLE);
		ivCorrect.setVisibility(View.INVISIBLE);
		ivMissing.setVisibility(View.INVISIBLE);

		final DataBaseHelper myDbHelper = new DataBaseHelper(this);
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					myDbHelper.createDataBase();
				} catch (IOException ioe) {

				}
				try {

					myDbHelper.openDataBase();

				} catch (SQLException sqle) {

					throw sqle;

				}
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						pb.setVisibility(View.GONE);
						tv.setVisibility(View.VISIBLE);
						clearButton.setVisibility(View.VISIBLE);
					}
				});
			}
		}).start();
		Log.i(TAG, "Words: "+myDbHelper.getReadableDatabase().rawQuery("SELECT * FROM words", null).getCount());
		
		tv.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String word = s.toString().toLowerCase();
				Log.i(TAG, "Searching for "+word);
				Cursor c = myDbHelper.getReadableDatabase().rawQuery("SELECT word FROM words WHERE word='"+word+"'", null);
				boolean contains = c.getCount() > 0;
				if (contains) {
					ivCorrect.setVisibility(View.VISIBLE);
					ivMissing.setVisibility(View.INVISIBLE);
					scoreText.setVisibility(View.VISIBLE);
					scoreText.setText("Score: " + getScore(word));
				} else if (word.isEmpty()) {
					ivCorrect.setVisibility(View.INVISIBLE);
					scoreText.setVisibility(View.INVISIBLE);
					ivMissing.setVisibility(View.INVISIBLE);
				} else {
					ivCorrect.setVisibility(View.INVISIBLE);
					scoreText.setVisibility(View.INVISIBLE);
					ivMissing.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private static SparseIntArray scores = new SparseIntArray(26);
	static {
		scores.append('a', 1);
		scores.append('b', 3);
		scores.append('c', 3);
		scores.append('d', 2);
		scores.append('e', 1);
		scores.append('f', 4);
		scores.append('g', 2);
		scores.append('h', 4);
		scores.append('i', 1);
		scores.append('j', 8);
		scores.append('k', 5);
		scores.append('l', 1);
		scores.append('m', 3);
		scores.append('n', 1);
		scores.append('o', 1);
		scores.append('p', 3);
		scores.append('q', 10);
		scores.append('r', 1);
		scores.append('s', 1);
		scores.append('t', 1);
		scores.append('u', 1);
		scores.append('v', 4);
		scores.append('w', 4);
		scores.append('x', 8);
		scores.append('y', 4);
		scores.append('z', 10);
	}

	protected int getScore(String word) {
		int total = 0;
		for (int i = 0; i < word.length(); i++) {
			total += scores.get(word.charAt(i));
		}
		return total;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
