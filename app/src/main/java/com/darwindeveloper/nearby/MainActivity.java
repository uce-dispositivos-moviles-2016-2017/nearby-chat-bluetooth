package com.darwindeveloper.nearby;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darwindeveloper.nearby.base_datos.ChatContract;
import com.darwindeveloper.nearby.base_datos.ChatDbHelper;
import com.darwindeveloper.nearby.base_datos.ExtrasSQLite;
import com.darwindeveloper.nearby.chat.ChatAdapter;
import com.darwindeveloper.nearby.chat.SMS;
import com.darwindeveloper.nearby.extras.Dispositivo;
import com.darwindeveloper.nearby.extras.SpinnerAdapter;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickedListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, ChatAdapter.OnCheckBoxItemClickListener, ChatAdapter.OnLongItemClickListener {


    public static boolean is_sms_select = false;//si hay algun sms del chat seleccionado podra ser acedida desde una clase exteriror(Adoater del recyclerview)
    private boolean is_visible;//lamacenara true si la actividad se esta mostrando
    //o false si se encuentra en estado de pausa o no visible, esto es util para mostrar las notificaciones cunado la actividad no esta visible pero se esta ejecutando la aplicacion

    private Toolbar toolbar;//tolbar
    private ArrayList<Dispositivo> dispositivos_vinculados = new ArrayList<>();//lista de todos los dispositivos vinculados al telefono

    private ImageView status_connected;


    private SQLiteDatabase db;//para realizar las tareas en la base de datos
    private ArrayList<SMS> list_sms = new ArrayList<>();//lista de mensajes recuperados de la base de datoas
    private ArrayList<SMS> list_sms_select = new ArrayList<>();//lista de los mensajes seleccionaddos para eliminar
    private boolean todos_seleccionados;//si todos los mensajes de una chat estan seleccionados y agregadoa al arraylist anterior
    private RecyclerView recyclerViewCHAT;//en donde se mostraran los mensajes del chat
    private ChatAdapter mChatAdapter;//adaptador para el recyclerview anterior
    LoadChat mLoadChat;//tarea asincrota para cargar los mensajes del chat


    // tipos de mensajes que se enviara el BluetoothChatService a nuestro Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_READ_IMAGE = 3;
    public static final int MESSAGE_WRITE = 4;
    public static final int MESSAGE_WRITE_IMAGE = 5;
    public static final int MESSAGE_DEVICE_NAME = 6;
    public static final int MESSAGE_TOAST = 7;
    public static final int MESSAGE_CONNECTED = 8;


    // nombres clave recividos desde BluetoothChatService a nuestro Handler
    public static final String DEVICE_NAME = "com.darwindeveloper.nearby.device_name";
    public static final String DEVICE_ADRESS = "com.darwindeveloper.nearby.device_address";
    public static final String TOAST = "toast";


    //
    private static final int REQUEST_ENABLE_BT = 2;//clave para activar el bluetooth con startActivityForResult
    private static final int REQUEST_GET_DEVICE_TO_SEARCH = 3;//para obtener un dispositivo bluedo por medio del metodo startActivityForResult
    private static final int REQUEST_GET_FILE_FROM_SDCARD = 4;//para obtener un archivo por medio de startActivityForResult
    private static final int REQUEST_GET_IMAGE_FROM_CAMERA = 5;//para obtener una foto con la camara por medio de startActivityForResult
    private static final int REQUEST_GET_AUDIO_RECORD = 6;
    private static final int REQUEST_GET_VIDEO_RECORD = 7;
    private static final int REQUEST_SEND_FILE = 8;//para enviar un archivo por bluettoth y ontener el resultado


    private static final int REQUEST_PERMISSION = 1;//para android M o superior

    private BluetoothAdapter mBluetoothAdapter;//adaptador bluetooth para detectar dispositivos y realizar operaciones bluetooth
    private Spinner spinner_devices;//spinner que mostrara la lista de dispositivos vinculados


    private StringBuffer mOutStringBuffer;// String buffer para los mensajes de salida


    // se encarga del servicio de chat
    private BluetoothChatService mChatService = null;
    private String MY_MAC;//almacena my mac address del bluetooth
    private String TO_MAC;//almacena el mac address del bluetooth con el que nos vamos a connectar
    private String TO_NAME = "nearby";


    // Handler que obtinen informacion de vuelta del BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    Toast.makeText(MainActivity.this, "Mensaje Enviado", Toast.LENGTH_SHORT).show();
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);

                    //agregamos el mensaje enviado a la base de datos
                    if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        Long last_id = ExtrasSQLite.insertNewSmsChat(db, "YO", MY_MAC, TO_MAC, writeMessage);
                        if (last_id != -1) {
                            list_sms.add(new SMS(last_id, "YO", writeMessage, ExtrasSQLite.getDateTime()));
                            mChatAdapter.notifyItemInserted(list_sms.size() - 1);
                            mChatAdapter.notifyDataSetChanged();
                            recyclerViewCHAT.scrollToPosition(list_sms.size() - 1);
                        }
                    }


                    break;
                case MESSAGE_READ:


                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);


                    //agregamos el mensaje recivido a la base de datos
                    if (mChatService.getState() == BluetoothChatService.STATE_CONNECTED) {
                        Long last_ide = ExtrasSQLite.insertNewSmsChat(db, "EL", MY_MAC, TO_MAC, readMessage);
                        if (last_ide != -1) {
                            list_sms.add(new SMS(last_ide, "EL", readMessage, ExtrasSQLite.getDateTime()));
                            mChatAdapter.notifyItemInserted(list_sms.size() - 1);
                            mChatAdapter.notifyDataSetChanged();
                            recyclerViewCHAT.scrollToPosition(list_sms.size() - 1);
                        }

                    }

                    if (!is_visible) {
                        mostrar_notificacion(readMessage);
                    } else {
                        //Toast.makeText(MainActivity.this, "Tienes un nuevo Mensaje", Toast.LENGTH_SHORT).show();
                        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(100);
                        Toast.makeText(MainActivity.this, "Tienes un nuevo sms", Toast.LENGTH_SHORT).show();
                    }

                    break;


                case MESSAGE_READ_IMAGE:
                    Toast.makeText(MainActivity.this, "imagen recivida", Toast.LENGTH_SHORT).show();
                    break;

                case MESSAGE_DEVICE_NAME:

                    status_connected.setImageResource(R.drawable.connected);
                    TO_NAME = msg.getData().getString(DEVICE_NAME);
                    // save the connected device's name
                    TO_MAC = msg.getData().getString(DEVICE_ADRESS);
                    mostrarSnackBar("CONECTADO A " + msg.getData().getString(DEVICE_NAME), android.R.color.holo_green_dark);
                    spinner_devices.setSelection(buscarDeviceEnSpinner(TO_MAC), true);
                    if (mLoadChat.getStatus() == AsyncTask.Status.RUNNING || mLoadChat.getStatus() == AsyncTask.Status.FINISHED) {
                        mLoadChat = null;
                    }
                    mLoadChat = new LoadChat();
                    mLoadChat.execute();

                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();


                    try {
                        if (msg.getData().getString(TOAST).equals("Coneccion perdida")) {
                            reload_app();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    //para trabajar con los emoticones
    private EmojiPopup emojiPopup;
    private EmojiEditText editText;
    private ViewGroup rootView;//content_main
    private ImageButton emojiButton;

    /**
     * preparamos el editText para trabajar con los emoticones
     */
    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {
                Log.d("MainActivity", "Clicked on Backspace");
            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {
                Log.d("MainActivity", "Clicked on emoji");
            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                emojiButton.setImageResource(R.drawable.ic_keyboard);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {
                Log.d("MainActivity", "Opened soft keyboard");
            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                emojiButton.setImageResource(R.drawable.ic_emoticon);
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(editText);


        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage();
                    handled = true;
                }
                return handled;
            }
        });

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }


    @Override
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        is_visible = true;//si la actividad es visible

        //preparamos la base de datos
        ChatDbHelper mDbHelper = new ChatDbHelper(MainActivity.this);
        // Gets the data repository in write mode
        db = mDbHelper.getWritableDatabase();
        mLoadChat = new LoadChat();


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);//escondemos el titulo en el toolbar


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//inicializamos el adaptador Bluetooth

        //inicializamos las vistas
        spinner_devices = (Spinner) findViewById(R.id.spinner_devices);
        recyclerViewCHAT = (RecyclerView) findViewById(R.id.recyclerview_chat);
        editText = (EmojiEditText) findViewById(R.id.edit_text_sms);
        rootView = (ViewGroup) findViewById(R.id.content_chat);
        emojiButton = (ImageButton) findViewById(R.id.btn_emoji);
        status_connected = (ImageView) findViewById(R.id.image_connected);


        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                emojiPopup.toggle();
            }
        });


        // RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(context, 2);
        recyclerViewCHAT.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerViewCHAT.setItemAnimator(new DefaultItemAnimator());
        mChatAdapter = new ChatAdapter(MainActivity.this, list_sms);
        mChatAdapter.setOnCheckBoxItemClickListener(this);
        mChatAdapter.setOnLongItemClickListener(this);
        recyclerViewCHAT.setAdapter(mChatAdapter);


        //para detectar si el teclado esta visible en pantalla
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();

                LinearLayout.LayoutParams params_content_write = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                LinearLayout.LayoutParams params_recyclerview_chat = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


                //contenedor el cual continene el edittext para ingresar el mensaje a enviar
                LinearLayout content_write_sms = (LinearLayout) findViewById(R.id.layout_content_write_sms);


                // if more than 200 dp, it's probably a keyboard is visible..
                if (heightDiff > dpToPx(200)) {// si el teclado esta visible cambiamos los pesos del recyclerview que contiene los sms y el contenedor para ingresar el texto a enviar
                    params_recyclerview_chat.weight = 2;
                    params_content_write.weight = 1;
                } else {
                    params_recyclerview_chat.weight = 1;
                    params_content_write.weight = 4;
                }
                //aplicamos los cambios
                content_write_sms.setLayoutParams(params_content_write);
                recyclerViewCHAT.setLayoutParams(params_recyclerview_chat);
            }
        });


        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth no esta disponible", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        MY_MAC = mBluetoothAdapter.getAddress();


        //PREPARAMOS EL SPINNER
        setUpSpinnerDevicesBluetooth();


        //si el tutorial de como usar la app ya se mostro con anterioridad no se muestra de nuevo
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        boolean is_tutorial_ok = preferences.getBoolean(getString(R.string.tutorial_ok), false);
        if (!is_tutorial_ok)
            tutorial();


        setUpEmojiPopup();

    }

    /**
     * transforma dp a px
     *
     * @param valueInDp valor en dp para convertir a pixeles
     * @return
     */
    private float dpToPx(float valueInDp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    setUpSpinnerDevicesBluetooth();
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "Para usar esta aplicacion debes activar el Bluetooth ", Toast.LENGTH_SHORT).show();
                    finish();
                }

                break;

            case REQUEST_GET_DEVICE_TO_SEARCH:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Vinculando Dispositivos...", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_GET_FILE_FROM_SDCARD:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedfile = data.getData();
                    send_file(selectedfile);
                }
                break;

            case REQUEST_GET_IMAGE_FROM_CAMERA:
                if (resultCode == Activity.RESULT_OK) {

                    if (mCurrentPhotoPath != null) {
                        send_file(imageToUploadUri);
                    }
                }
                break;


            case REQUEST_SEND_FILE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Archivo Enviado", Toast.LENGTH_SHORT).show();
                }
                break;


            case REQUEST_GET_AUDIO_RECORD:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        Log.i("audio record", uri.toString());
                        send_file(uri);
                    } else {
                        Log.i("audio record", "NULL");
                    }
                }
                break;

            case REQUEST_GET_VIDEO_RECORD:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri != null) {
                        send_file(uri);
                        Log.i("video record", uri.toString());
                    }
                }
                break;

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarSnackBar("EXITO: ya puedes usar las funciones de camara y video", android.R.color.holo_green_dark);
            } else {
                Toast.makeText(this, "No puedes acceder a esta funcion sin los permisos necesarios", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reload) {
            reload_app();
        }

        if (id == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, SearchDevicesActivity.class);

            //una pequeña animacion para cuando se lance SearchDevicesActivity
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_animation, R.anim.right_animation).toBundle();

            startActivityForResult(intent, REQUEST_GET_DEVICE_TO_SEARCH, bndlanimation);
        }

        if (id == R.id.action_view) {
            //hacemos visible el dispositivo bluetooth para que otros lo encuentren
            Intent discoverableIntent = new
                    Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 600);
            startActivity(discoverableIntent);
        }


        if (id == R.id.action_info) {
            Intent intent = new Intent(MainActivity.this, InformacionActivity.class);

            //una pequeña animacion para cuando se lance SearchDevicesActivity
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_animation, R.anim.right_animation).toBundle();

            startActivity(intent, bndlanimation);
        }


        if (id == R.id.action_help) {
            Intent intent = new Intent(MainActivity.this, ManualDeUsuarioActivity.class);

            //una pequeña animacion para cuando se lance SearchDevicesActivity
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_animation, R.anim.right_animation).toBundle();
            startActivity(intent, bndlanimation);
        }

        if (id == R.id.action_dispositivo) {
            Intent intent = new Intent(MainActivity.this, MiDispositivoActivity.class);

            //una pequeña animacion para cuando se lance SearchDevicesActivity
            Bundle bndlanimation =
                    ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.left_animation, R.anim.right_animation).toBundle();
            startActivity(intent, bndlanimation);
        }


        if (id == android.R.id.home) {
            //escondemos el menu que se mostro cunado se presiono largamente un sms en el chat
            is_sms_select = false;
            mChatAdapter.notifyDataSetChanged();//notificamos los cambios en el recyclerview

            //cambiamos al toolbar normal
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_main);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            spinner_devices.setVisibility(View.VISIBLE);
        }


        if (id == R.id.action_delete) {
            //mostramos un dialogo de confirmacion
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setIcon(R.drawable.ic_delete).setTitle("Mensaje de Confirmación")
                    .setMessage("¿Seguro que desea eliminar los mensajes seleccionados?")
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //lanzamos una tarea para eliminar los mensajes
                            new DeleteSms().execute(list_sms_select);

                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.create().show();

        }


        if (id == R.id.action_marcar_todos) {
            if (todos_seleccionados) {//si todos los mensjaes del chat estan marcados los desmarcamos
                for (SMS sms : list_sms) {
                    sms.setIs_selected(false);
                }
                list_sms_select.clear();
            } else {
                list_sms_select.clear();
                for (SMS sms : list_sms) {
                    sms.setIs_selected(true);
                    list_sms_select.add(sms);
                }
            }

            todos_seleccionados = !todos_seleccionados;

            mChatAdapter.notifyDataSetChanged();
        }


        return true;
    }


    /**
     * se ejecuta cuando se selecciona un elelemnto del spinner
     *
     * @param parent contenendor padre
     * @param view   vista
     * @param pos    posicion
     * @param id
     */
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        if (parent.getId() == spinner_devices.getId()) {
            if (pos != 0) {//si hay un dispositivo bluetooth seleccionado en el spinner
                if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
                    TO_MAC = dispositivos_vinculados.get(pos).getMAC();//recuperamos la direccion mac del otro dispositivo bluetooth
                    mChatService.connect(mBluetoothAdapter.getRemoteDevice(TO_MAC));
                    if (mLoadChat.getStatus() == AsyncTask.Status.RUNNING || mLoadChat.getStatus() == AsyncTask.Status.FINISHED) {
                        mLoadChat = null;
                    }
                    mLoadChat = new LoadChat();
                    mLoadChat.execute();
                } else {
                    spinner_devices.setSelection(buscarDeviceEnSpinner(BluetoothChatService.ADDRESS_CONNECTED), true);
                }
            } else {
                TO_MAC = null;
                // mChatService.stop();
            }

        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }


    //MIS  METODOS

    /**
     * reinicia la actividad actual eliminadando la pila de actividades
     */
    private void reload_app() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    /**
     * muestra una notificacion y cuando se de click en esta se renaura el chat previo sin actualizar la actividad
     *
     * @param sms mensaje que se mostrar en la notificacion
     */
    private void mostrar_notificacion(String sms) {


        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(300);


        //hacemos sonar el sonido de notificacion
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();

        // Sets an ID for the notification, so it can be updated
        int notifyID = 0;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(TO_NAME + ": Nuevo mensaje")
                        .setContentText(sms);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(MainActivity.this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), (int) System.currentTimeMillis(), resultIntent, 0);

        mBuilder.setContentIntent(pendingIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.

        mNotificationManager.notify(notifyID, mBuilder.build());
    }

    /**
     * este metodo muestra un targetView con las partes fundamentales de la app
     */
    private void tutorial() {

        // We load a drawable and create a location to show a tap target here
        // We need the display to get the width and height at this point in time
        final Display display = getWindowManager().getDefaultDisplay();
        // Load our little droid guy
        final Drawable droid = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        // Tell our droid buddy where we want him to appear
        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
        // Using deprecated methods makes you look way cool
        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);

        final SpannableString sassyDesc = new SpannableString("It allows you to go back, sometimes");
        sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length() - "somtimes".length(), sassyDesc.length(), 0);


        TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forBounds(droidTarget, "Bienvenido a Nearby", "Nearby es una aplicacion que te permite chatear por medio de Bluetooth con otros dispositivos android con Nearby")
                                .targetCircleColor(R.color.colorAccent)
                                .cancelable(false)
                                .icon(droid),
                        TapTarget.forView(findViewById(R.id.spinner_devices), "Dispositivos Vinculados", "Esta opcion te muestra un menu con una lista todos los dispositivos bluetooth vinculados a tu dispositivo android")
                                .targetCircleColor(R.color.colorAccent)
                                .cancelable(false),
                        TapTarget.forView(findViewById(R.id.edit_text_sms), "Escribe aqui tu mensaje", "Cuando estes conectado con un dispositivo tu estas listo para redactar tu mensaje en esta casilla de texto")
                                .targetCircleColor(R.color.colorAccent)
                                .cancelable(false),

                        TapTarget.forToolbarOverflow(toolbar, "Menu de opciones", "Para mas informacion sobre como usar la app, ajustes y los autores de la misma")
                                .targetCircleColor(R.color.colorAccent)
                                .cancelable(false),
                        TapTarget.forBounds(droidTarget, "Estas listo para usar a Nearby")
                                .targetCircleColor(android.R.color.holo_green_dark)
                                .icon(ContextCompat.getDrawable(this, android.R.drawable.ic_media_play))
                )

                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean(getString(R.string.tutorial_ok), true);
                        editor.apply();
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                });

        sequence.start();

    }

    /**
     * preparamos el spinner y lo llenamos con una lista de los dispositivos vinculados
     */
    private void setUpSpinnerDevicesBluetooth() {
        //PREPARAMOS EL SPINNER
        dispositivos_vinculados = getDevices();
        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(MainActivity.this, R.layout.item_sppiner_device, dispositivos_vinculados);
        spinner_devices.setAdapter(spinnerAdapter);
        spinner_devices.setOnItemSelectedListener(this);

    }


    /**
     * muestra un snackar personalizado
     *
     * @param sms   texto a mostrar
     * @param color color de fondo del snackbar
     */
    private void mostrarSnackBar(String sms, int color) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), sms, Snackbar.LENGTH_SHORT);
        snackbar.setAction("Action", null).show();
        snackbar.setDuration(400);
        snackbar.getView().setBackgroundColor(getResources().getColor(color));
        snackbar.show();
    }

    /**
     * busca un SMS en un arraylist de SMS
     *
     * @param smss arraylist en donde se buscara el mensaje
     * @param ID   id (que ocupa en la base de datos) del mensaje a buscar
     * @return
     */
    private int buscarSMS(ArrayList<SMS> smss, long ID) {
        int pos = -1;

        for (int i = 0; i < smss.size(); i++) {
            if (smss.get(i).getID() == ID) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /**
     * busca un dispositivo bluetooth en nuestro array de spinners
     *
     * @param adress parametro con el cual se realizara la busqueda
     * @return
     */
    private int buscarDeviceEnSpinner(String adress) {
        int pos = -1;

        for (int i = 0; i < dispositivos_vinculados.size(); i++) {
            if (dispositivos_vinculados.get(i).getMAC().equals(adress)) {
                pos = i;
                break;
            }
        }
        return pos;
    }


    /**
     * @return lista de dispositivos vinculados al telefono
     */
    private ArrayList<Dispositivo> getDevices() {


        ArrayList<Dispositivo> dispositivos_vinculados = new ArrayList<>();

        dispositivos_vinculados.add(new Dispositivo("un dispositivo", "Seleccione"));


        //una coleccion de todos loas dispositivos vinculados al telefono android
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        // si hay dispositivos vinculados
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                Dispositivo tmp = new Dispositivo(device.getAddress(), device.getName());
                dispositivos_vinculados.add(tmp);
            }
        }

        return dispositivos_vinculados;
    }


    /**
     * inicializa los servicios del chat bluetooth
     */
    private void setupChat() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    /**
     * comprueba la connecion y envia un mensaje al dispositivo conectado
     */
    private void sendMessage() {

        String message = editText.getText().toString();//obtenemos el texto escrito en el editText del mensaje

        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "NO estas conectado con un dispositivo", Toast.LENGTH_SHORT).show();
            return;
        }
        // comprobamos que no enviemos un mensaje vacio
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.send_sms(send);
            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            editText.setText(mOutStringBuffer);

        }
    }


    /**
     * muestra los chekboxes en canda un de los sms del chat
     */
    private void mostrar_checkboxes() {
        //escondemos el spinner
        spinner_devices.setVisibility(View.GONE);
        //tenemos un sms del chat seleccionado
        is_sms_select = true;
        mChatAdapter.notifyDataSetChanged();//notificamos los cambios en el recyclerview

        //cambiamos del toolbar normal al toolbar sms chat
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_sms_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }


    /**
     * obtenemos una imagen del almacenamiento interno
     */
    public void pick_file(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, REQUEST_GET_FILE_FROM_SDCARD);
    }

    /**
     * tomamos una foto con la camara para enviarla por bluettoth
     */
    Uri imageToUploadUri;

    public void pick_photo(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (hasPermissionInManifest(Manifest.permission.CAMERA)) {
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //si hay una palicacion disponible paara realizar esta accion
                File f = null;
                try {
                    f = createImageFile();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    imageToUploadUri = Uri.fromFile(f);
                    startActivityForResult(takePictureIntent, REQUEST_GET_IMAGE_FROM_CAMERA);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No se encontro una aplicacion de camara", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(this, "No se pudo acceder a la camara", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION);
            }
        }
    }


    public void pick_audio_record(View v) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        if (intent.resolveActivity(getPackageManager()) != null) {
            //si hay una palicacion disponible paara realizar esta accion
            startActivityForResult(intent, REQUEST_GET_AUDIO_RECORD);
        } else {
            Toast.makeText(this, "No se encontro una aplicacion para grabar audio", Toast.LENGTH_LONG).show();
        }
    }


    public void pick_video_record(View v) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //controlamos los posibles errores en tiempo de ejecucion para android M o superior
        if (hasPermissionInManifest(Manifest.permission.CAMERA)) {
            if (intent.resolveActivity(getPackageManager()) != null) {
                //si hay una palicacion disponible paara realizar esta accion
                startActivityForResult(intent, REQUEST_GET_VIDEO_RECORD);
            } else {
                Toast.makeText(this, "No se encontro una aplicacion para grabar audio", Toast.LENGTH_LONG).show();
            }
        } else {
            //Toast.makeText(this, "No se pudo acceder a la camara", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA},
                        REQUEST_PERMISSION);
            }
        }


    }

    /**
     * enviamos un archivo por blueetooth
     *
     * @param uri
     */
    private void send_file(Uri uri) {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setType("image/jpeg");
            intent.setPackage("com.android.bluetooth");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivityForResult(intent, REQUEST_SEND_FILE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".jpg");

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    /**
     * compureva si tenemos los permisos apoara relizar una determinadad acccion (para mandroid M o superior)
     *
     * @param permissionName
     * @return
     */
    public boolean hasPermissionInManifest(String permissionName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//si la version de android es superior a android M
            if (checkSelfPermission(permissionName) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }


    /**
     * convierte la Uri con nuestra imagen a un array de bytes
     *
     * @param uri
     * @return
     * @throws IOException
     */
    private synchronized byte[] parseUriToBytes(Uri uri) throws IOException {
        // this dynamically extends to take the bytes you read
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }


    //CICLO DE VIDA DE LA ACTIVIDAD
    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null) {
                setupChat();
            }
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        is_visible = true;
        if (mChatService != null) {
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }


    @Override
    public synchronized void onPause() {
        super.onPause();
        is_visible = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        is_visible = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        is_sms_select = false;
    }


    // FIN CICLO DE VIDA DE LA ACTIVIDAD


    @Override
    public void onCheckBoxItemClick(CheckBox checkbok, SMS sms, int position) {

        if (checkbok.isChecked()) {
            list_sms_select.add(sms);

        } else {
            //buscamos el sms en el arraylist y luego lo removemos
            int pos = buscarSMS(list_sms_select, sms.getID());
            if (pos != -1) {
                list_sms_select.remove(pos);
            }
        }

        //mChatAdapter.notifyDataSetChanged();

        getSupportActionBar().setTitle("selecionados " + list_sms_select.size());

    }


    /**
     * si se presiona de manera prolongada un mensaje del chat
     *
     * @param view     la vista sms
     * @param sms      un objeto de la clase SMS
     * @param position el indice del sms precionado
     */
    @Override
    public void onLongSmsItemClick(View view, final SMS sms, final int position) {
        //preparamos el context menu
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.popup_menu_sms,
                popup.getMenu());//definimos el context menu
        popup.show();//mostramos el context menu


        //capturamos los click en el item del context menu
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.popup_action_marcar:
                        mostrar_checkboxes();
                        break;
                    case R.id.popup_action_eliminar:
                        ArrayList<SMS> tmp = new ArrayList<SMS>();
                        tmp.add(sms);
                        new DeleteSms().execute(tmp);
                        break;

                    case R.id.popup_action_copy_text:
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setText(sms.getMessage());
                        Toast.makeText(MainActivity.this, "texto copiado", Toast.LENGTH_SHORT).show();
                        break;
                }

                return true;
            }
        });
    }


    //MIS CLASES INTERNAS
    private class LoadChat extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            //limpiamos el chat con la antigua conversacion
            list_sms.clear();
            //eliminanmos los mensajes viejos del chat para remplazarlos con los nuevos
            mChatAdapter.notifyItemRangeRemoved(0, mChatAdapter.getItemCount() - 1);
            mChatAdapter.notifyDataSetChanged();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Cursor cursor = ExtrasSQLite.getChat(db, MY_MAC, TO_MAC);
            //recuperamos los mensajes del chat y los almacenamos en un array list
            if (cursor.moveToFirst()) {
                String[] columnNames = cursor.getColumnNames();
                do {

                    //variables para crear un objetode la clase SMS e agregarlos a la lista de mensajes
                    String yo = null, el = null, sms = null, date_time = null, from = null;
                    long id = -1;

                    for (String columnName : columnNames) {
                        String column = cursor.getString(cursor.getColumnIndex(columnName));


                        if (columnName.equals(ChatContract.ChatsEntry._ID)) {
                            id = Long.parseLong(column);
                        }

                        if (columnName.equals(ChatContract.ChatsEntry.YO)) {
                            yo = column;
                        }

                        if (columnName.equals(ChatContract.ChatsEntry.EL)) {
                            el = column;
                        }


                        if (columnName.equals(ChatContract.ChatsEntry.FROM)) {
                            from = column;
                        }

                        if (columnName.equals(ChatContract.ChatsEntry.SMS)) {
                            sms = column;
                        }

                        if (columnName.equals(ChatContract.ChatsEntry.DATE_TIME)) {
                            date_time = column;
                        }

                    }

                    //agregamos el mensaje al chat
                    list_sms.add(new SMS(id, from, sms, date_time));


                } while (cursor.moveToNext());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //notificamos los cambios al adapter
            mChatAdapter.notifyItemRangeInserted(0, list_sms.size() - 1);
            mChatAdapter.notifyDataSetChanged();
            recyclerViewCHAT.scrollToPosition(list_sms.size() - 1);
        }
    }

    private class DeleteSms extends AsyncTask<ArrayList<SMS>, Void, Void> {

        private ArrayList<Integer> tmp_posiciones = new ArrayList<>();


        @SafeVarargs
        @Override
        protected final Void doInBackground(ArrayList<SMS>... params) {

            for (SMS sms : params[0]) {
                //eliminamos el mensaje de la base de datos
                boolean result = ExtrasSQLite.delete_sms(db, Long.toString(sms.getID()));
                if (result) {
                    //eliminamos el mensaje del chat
                    int pos = buscarSMS(list_sms, sms.getID());

                    //removemos el mensaje de la lista del chat
                    if (pos != -1) {
                        list_sms.remove(pos);
                        tmp_posiciones.add(pos);
                    }


                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            //actualizamos el recyclerview borrando todos los mensajes eliminados de la base de datos
            list_sms_select.clear();

            for (int i = 0; i < tmp_posiciones.size(); i++) {
                mChatAdapter.notifyItemRemoved(tmp_posiciones.get(i));
            }
            mChatAdapter.notifyDataSetChanged();
            try {
                getSupportActionBar().setTitle("NO S.");
            } catch (Exception e) {
                Log.e("Error delete sms", e.getMessage());
            }

            Toast.makeText(MainActivity.this, "Mensajes Eliminados", Toast.LENGTH_SHORT).show();

        }

        private void notifyChatAdater(int position) {


        }
    }
    //FIN CLASES INTERNAS

}
