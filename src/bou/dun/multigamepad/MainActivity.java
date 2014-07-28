package bou.dun.multigamepad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity {

	static String serverAddress = "127.0.0.1";
	static private int playerNb = 0;
	static private int playerask = 0;
	private int where_am_i = 0;
	private BufferedReader in;
	private PrintWriter out;
	SharedPreferences preferences;
	
	int[] pos_pad = {	R.drawable.idirpadul,
						R.drawable.idirpadu,
						R.drawable.idirpadur,
						R.drawable.idirpadl,
						R.drawable.idirpad,
						R.drawable.idirpadr,
						R.drawable.idirpaddl,
						R.drawable.idirpadd,
						R.drawable.idirpaddr};
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			wifiEnabler();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	public void wifiEnabler() throws IOException{
		WifiManager wifi = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		final boolean b=wifi.isWifiEnabled();
		if(!b){
			final AlertDialog.Builder builder = new AlertDialog.Builder(
					MainActivity.this);
			builder.setTitle("Wifi");
			builder.setMessage("L'application fonctionne grace au WiFi. Do you want to turn it on?");
			builder.setPositiveButton("activer WiFi",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(final DialogInterface dialogInterface,
								final int i) {
//							startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
							WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
							wifi.setWifiEnabled(true);		
							try {
								wifiEnabler();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});			
			builder.setNegativeButton("Quitter",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
							System.exit(0);
						}
					});
			builder.create().show();
		}else{
			loadLayoutItems();
			connectToServerOnLoad();	
		}
	}
	
	public void connectToServerOnLoad() throws IOException{
		preferences = getSharedPreferences("MGP",MODE_PRIVATE);
		if (preferences.getString("ServerIp", "") == "") {
			try {
				connectToServer();
			} catch (IOException e) {
				connectToServer();
			}
		} else {
			serverAddress = preferences.getString("ServerIp", "");
			Socket socket;
			try {
				socket = new Socket(serverAddress, 9898);
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				String inread = in.readLine();
				((TextView) findViewById(R.id.player)).setText(inread);
				playerNb = Integer.valueOf(inread.replace("Player ", ""));
			}catch (IOException e) {
				connectToServer();
			}
		}
	}
	
	public Integer toBinary(String in){
		byte[] inb = in.getBytes();
		String retour = "";
		for (byte b : inb) {
			retour+= Integer.toBinaryString(b);
        }
		return Integer.parseInt(retour);
	}
	public void loadLayoutItems(){
		WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		getWindow().setAttributes(attrs);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		preferences = getSharedPreferences("MGP",MODE_PRIVATE);
		String theme = preferences.getString("theme", "classical");
		
//		Integer custom1 = toBinary("custom1");
		
		if(theme.equals("classical")){
			setContentView(R.layout.activity_main_classic);
		}else{
//			switch (toBinary(theme)) {
//			case custom1:
//				pos_pad = new int[]{R.drawable.idirpadul,R.drawable.idirpadu,R.drawable.idirpadur,R.drawable.idirpadl,R.drawable.idirpad,R.drawable.idirpadr,R.drawable.idirpaddl,R.drawable.idirpadd,R.drawable.idirpaddr};
//				setContentView(R.layout.activity_main_custom1);
//				break;
//			}
		}
		
//		setContentView(R.layout.activity_main);
		
		defineActionOnButtons();
	}
	
	public void defineActionOnButtons(){
		final Vibrator vib = (Vibrator) this
				.getSystemService(Context.VIBRATOR_SERVICE);
		((Button) findViewById(R.id.L1))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d0");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u0");
						}
						return false;
					}
				});
		((Button) findViewById(R.id.L2))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d1");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u1");
						}
						return false;
					}
				});
		((Button) findViewById(R.id.R1))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d2");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u2");
						}
						return false;
					}
				});
		((Button) findViewById(R.id.R2))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d3");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u3");
						}
						return false;
					}
				});
		((ImageButton) findViewById(R.id.start))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d4");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u4");
						}
						return false;
					}
				});
		((ImageButton) findViewById(R.id.select))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d5");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u5");
						}
						return false;
					}
				});
		final Rect r = new Rect();
		final RelativeLayout table_pad = ((RelativeLayout)findViewById(R.id.table_pad));
		((RelativeLayout)findViewById(R.id.table_pad)).setOnTouchListener(new OnTouchListener() {			
			public boolean onTouch(View v, MotionEvent event) {
				v.getHitRect(r);		
				int width = r.width()/3;
				int height = r.height()/3;
				double percent_prec = 0.25;
				Rect z1 = new Rect(0, 									0, 			width+(int)(width*percent_prec), 		height+(int)(height*percent_prec));
				Rect z2 = new Rect(width, 								0, 			width*2, 	height);
				Rect z3 = new Rect(width*2-(int)(width*percent_prec), 	0, 			width*3, 	height+(int)(height*percent_prec));
				Rect z4 = new Rect(0, 		height, 	width, 		height*2);
				Rect z5 = new Rect(width+(int)(width*percent_prec), 	height+(int)(height*percent_prec), 	width*2-(int)(width*percent_prec), 	height*2-(int)(height*percent_prec));
				Rect z6 = new Rect(width*2, height, 	width*3, 	height*2);
				Rect z7 = new Rect(0, 		height*2-(int)(height*percent_prec), 	width+(int)(width*percent_prec), 		height*3);
				Rect z8 = new Rect(width, 	height*2, 	width*2, 	height*3);
				Rect z9 = new Rect(width*2-(int)(width*percent_prec), height*2-(int)(height*percent_prec), 	width*3, 	height*3);
				
				final float x = event.getX() + r.left;
	            final float y = event.getY() + r.top;
	            
	            
	            
	            int action = event.getAction();
				if (action == MotionEvent.ACTION_DOWN) {
					vib.vibrate(50);
					if (z1.contains((int) x, (int) y)){if(where_am_i != 1 )where_am_i = 1;}
					if (z2.contains((int) x, (int) y)){if(where_am_i != 2 )where_am_i = 2;}
					if (z3.contains((int) x, (int) y)){if(where_am_i != 3 )where_am_i = 3;}
					if (z4.contains((int) x, (int) y)){if(where_am_i != 4 )where_am_i = 4;}
					if (z5.contains((int) x, (int) y)){if(where_am_i != 5 )where_am_i = 5;}
					if (z6.contains((int) x, (int) y)){if(where_am_i != 6 )where_am_i = 6;}
					if (z7.contains((int) x, (int) y)){if(where_am_i != 7 )where_am_i = 7;}
					if (z8.contains((int) x, (int) y)){if(where_am_i != 8 )where_am_i = 8;}
					if (z9.contains((int) x, (int) y)){if(where_am_i != 9 )where_am_i = 9;}
					
					
					switch (where_am_i) {
					case 1:out.println("d6");out.println("d7");	table_pad.setBackgroundResource(pos_pad[0]);break;
					case 2:out.println("d6");					table_pad.setBackgroundResource(pos_pad[1]);break;
					case 3:out.println("d6");out.println("d9");	table_pad.setBackgroundResource(pos_pad[2]);break;
					case 4:out.println("d7");					table_pad.setBackgroundResource(pos_pad[3]);break;
					case 5:										table_pad.setBackgroundResource(pos_pad[4]);break;
					case 6:out.println("d9");					table_pad.setBackgroundResource(pos_pad[5]);break;
					case 7:out.println("d7");out.println("d8");	table_pad.setBackgroundResource(pos_pad[6]);break;
					case 8:out.println("d8");					table_pad.setBackgroundResource(pos_pad[7]);break;
					case 9:out.println("d8");out.println("d9");	table_pad.setBackgroundResource(pos_pad[8]);break;
					}
					
					
		            
		            
				} else if (action == MotionEvent.ACTION_MOVE) {
					int old_where_am_i = where_am_i;
					
					if (z1.contains((int) x, (int) y)){if(where_am_i != 1 )where_am_i = 1;}
					if (z2.contains((int) x, (int) y)){if(where_am_i != 2 )where_am_i = 2;}
					if (z3.contains((int) x, (int) y)){if(where_am_i != 3 )where_am_i = 3;}
					if (z4.contains((int) x, (int) y)){if(where_am_i != 4 )where_am_i = 4;}
					if (z5.contains((int) x, (int) y)){if(where_am_i != 5 )where_am_i = 5;}
					if (z6.contains((int) x, (int) y)){if(where_am_i != 6 )where_am_i = 6;}
					if (z7.contains((int) x, (int) y)){if(where_am_i != 7 )where_am_i = 7;}
					if (z8.contains((int) x, (int) y)){if(where_am_i != 8 )where_am_i = 8;}
					if (z9.contains((int) x, (int) y)){if(where_am_i != 9 )where_am_i = 9;}
					if(old_where_am_i != where_am_i ) {
						vib.vibrate(50);
						switch (old_where_am_i) {
							case 1:if(where_am_i != 2)out.println("u6"); if(where_am_i != 4)out.println("u7");break;
							case 2:if(where_am_i != 1 && where_am_i != 3)out.println("u6");break;
							case 3:if(where_am_i != 2)out.println("u6");if(where_am_i != 3)out.println("u9");break;
							case 4:if(where_am_i != 1 && where_am_i != 7)out.println("u7");break;
							case 5:break;
							case 6:if(where_am_i != 3 && where_am_i != 9)out.println("u9");break;
							case 7:if(where_am_i != 4)out.println("u7");if(where_am_i != 8)out.println("u8");break;
							case 8:if(where_am_i != 7 && where_am_i != 9)out.println("u8");break;
							case 9:if(where_am_i != 8)out.println("u8");if(where_am_i != 6)out.println("u9");break;
						}
						switch (where_am_i) {						
							case 1:out.println("d6");out.println("d7");	table_pad.setBackgroundResource(pos_pad[0]);break;
							case 2:out.println("d6");					table_pad.setBackgroundResource(pos_pad[1]);break;
							case 3:out.println("d6");out.println("d9");	table_pad.setBackgroundResource(pos_pad[2]);break;
							case 4:out.println("d7");					table_pad.setBackgroundResource(pos_pad[3]);break;
							case 5:										table_pad.setBackgroundResource(pos_pad[4]);break;
							case 6:out.println("d9");					table_pad.setBackgroundResource(pos_pad[5]);break;
							case 7:out.println("d7");out.println("d8");	table_pad.setBackgroundResource(pos_pad[6]);break;
							case 8:out.println("d8");					table_pad.setBackgroundResource(pos_pad[7]);break;
							case 9:out.println("d8");out.println("d9");	table_pad.setBackgroundResource(pos_pad[8]);break;
						}
					
					}
					
				}else if (action == MotionEvent.ACTION_UP) {
					switch (where_am_i) {
					case 1:out.println("u6");out.println("u7");break;
					case 2:out.println("u6");break;
					case 3:out.println("u6");out.println("u9");break;
					case 4:out.println("u7");break;
					case 5:break;
					case 6:out.println("u9");break;
					case 7:out.println("u7");out.println("u8");break;
					case 8:out.println("u8");break;
					case 9:out.println("u8");out.println("u9");break;
					}
					table_pad.setBackgroundResource(pos_pad[4]);
					where_am_i = 0;
				}
	            
				return false;
			}
		});
		
		((ImageButton) findViewById(R.id.triangle))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d10");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u10");
						}
						return false;
					}
				});
		((ImageButton) findViewById(R.id.carre))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d11");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u11");
						}
						return false;
					}
				});
		((ImageButton) findViewById(R.id.croix))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d12");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u12");
						}
						return false;
					}
				});
		((ImageButton) findViewById(R.id.cercle))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						vib.vibrate(50);
						int action = event.getAction();
						if (action == MotionEvent.ACTION_DOWN) {
							out.println("d13");
						} else if (action == MotionEvent.ACTION_UP) {
							out.println("u13");
						}
						return false;
					}
				});
		((TextView)findViewById(R.id.player)).setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				selectPlayer();
				return false;
			}
		});
		
		((ImageView)findViewById(R.id.settings)).setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = MotionEventCompat.getActionMasked(event);
			    switch(action) {
			        case (MotionEvent.ACTION_DOWN) :
			            return true;
			        case (MotionEvent.ACTION_MOVE) :
			            return true;
			        case (MotionEvent.ACTION_UP) :
			            openOptionsMenu();
			            return true;
			        case (MotionEvent.ACTION_CANCEL) :
			            return true;
			        case (MotionEvent.ACTION_OUTSIDE) :			          
			            return true;     
			        
			    }      
				return false;
			}
		});
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_settings:
			try {
				connectToServer();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.action_player: {
			selectPlayer();
			return true;
		}
		default:
			return false;
		}
	}

	public void selectPlayer() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.player, null);
		
		alertDialogBuilder.setView(promptsView);


		switch (playerNb) {
		case 1:
			((RadioButton) promptsView.findViewById(R.id.p1)).setChecked(true);
			break;
		case 2:
			((RadioButton) promptsView.findViewById(R.id.p2)).setChecked(true);
			break;
		case 3:
			((RadioButton) promptsView.findViewById(R.id.p3)).setChecked(true);
			break;
		case 4:
			((RadioButton) promptsView.findViewById(R.id.p4)).setChecked(true);
			break;
		}
		
		for(int i = 1; i <= 4; i++){
			out.println("a"+i);
			try {
				String inread = in.readLine();
//				boolean readin = Boolean.getBoolean(inread);
				
				if(inread.equals("true")){
					boolean readin = Boolean.getBoolean(inread);
					Log.d("PATATE", i+" => "+inread+" =>"+readin);
					switch (i) {
					case 1:
						((RadioButton) promptsView.findViewById(R.id.p1)).setEnabled(false);
						break;
					case 2:
						((RadioButton) promptsView.findViewById(R.id.p2)).setEnabled(false);
						break;
					case 3:
						((RadioButton) promptsView.findViewById(R.id.p3)).setEnabled(false);
						break;
					case 4:
						((RadioButton) promptsView.findViewById(R.id.p4)).setEnabled(false);
						break;
					}
				}				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.e("PATATE", "Player demandé : " + playerask);
						out.println("p"+playerask);
						try {
							String inread = in.readLine();
							((TextView) findViewById(R.id.player)).setText(inread);
							playerNb = Integer.valueOf(inread.replace("Player ", ""));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		Log.e("PATATE", "Actual player : " + playerNb);
		alertDialog.show();

	}

	public void onRadioButtonClicked(View view) {
		// Is the button now checked?
		boolean checked = ((RadioButton) view).isChecked();

		// Check which radio button was clicked
		switch (view.getId()) {
		case R.id.p1:
			if (checked) {
				Log.e("PATATE", "Player 1");
				playerask = 1;
			}
			break;
		case R.id.p2:
			if (checked) {
				Log.e("PATATE", "Player 2");
				playerask = 2;
			}
			break;
		case R.id.p3:
			if (checked) {
				Log.e("PATATE", "Player 3");
				playerask = 3;
			}
			break;
		case R.id.p4:
			if (checked) {
				Log.e("PATATE", "Player 4");
				playerask = 4;
			}
			break;
		}
	}

	public void connectToServer() throws IOException {

		// Get the server address from a dialog box.
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		LayoutInflater li = LayoutInflater.from(this);
		View promptsView = li.inflate(R.layout.prompts, null);
		alertDialogBuilder.setView(promptsView);
		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.editTextDialogUserInput);
		userInput.setText(serverAddress);
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to result
						// edit text
						serverAddress = userInput.getText().toString();
						Socket socket;
						try {
							socket = new Socket(serverAddress, 9898);
							in = new BufferedReader(new InputStreamReader(
									socket.getInputStream()));
							out = new PrintWriter(socket.getOutputStream(),
									true);
							String inread = in.readLine();
							((TextView) findViewById(R.id.player))
									.setText(inread);
							playerNb = Integer.valueOf(inread.replace(
									"Player ", ""));
							SharedPreferences preferences = getSharedPreferences(
									"MGP", MODE_PRIVATE);
							SharedPreferences.Editor edit = preferences.edit();
							edit.putString("ServerIp", serverAddress);
							edit.commit();

						} catch (IOException e) {
							final AlertDialog.Builder builder = new AlertDialog.Builder(
									MainActivity.this);
							builder.setTitle("Server not found !!");
							builder.setMessage("Enable to connect to the server, check your wifi connection !!");
							builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									finish();
									System.exit(0);
									
								}
							});
							builder.show();
						}

					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
		// Make connection and initialize streams

	}
	
	

}
