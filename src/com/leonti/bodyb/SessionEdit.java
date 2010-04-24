package com.leonti.bodyb;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SessionEdit extends Activity {
	
	private EditText mTitleText;
	private EditText mDescText;
	private Long mRowId;
	private Long mProgramsConnectorId;
    private ExcercisesDbAdapter mDbHelper;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new ExcercisesDbAdapter(this);
        mDbHelper.open();
        
        setContentView(R.layout.edit_session);
       
        mTitleText = (EditText) findViewById(R.id.edit_name);
        mDescText = (EditText) findViewById(R.id.edit_description);
      
        Button saveButton = (Button) findViewById(R.id.button_save);
       
        Bundle extras = getIntent().getExtras();
        mRowId = savedInstanceState != null ? savedInstanceState.getLong(ExcercisesDbAdapter.KEY_ROWID) 
                : null;

        if (mRowId == null && extras != null) {      	            
        	mRowId = extras.getLong(ExcercisesDbAdapter.KEY_ROWID);
        }
        
        mProgramsConnectorId = savedInstanceState != null ? savedInstanceState.getLong("programs_connector_id") 
                : null;
        
        if (mProgramsConnectorId == null && extras != null) {            
        	mProgramsConnectorId = extras.getLong("programs_connector_id");
        }
        
        populateFields();
       
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
          
        });
     }
    
    private void populateFields() {
        if (mRowId != null && mRowId > 0) {

        	
            Cursor session = mDbHelper.fetchSession(mRowId);
            startManagingCursor(session);
            mTitleText.setText(session.getString(
                        session.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_TITLE)));
            mDescText.setText(session.getString(
                    session.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_DESC)));
            
            mProgramsConnectorId = session.getLong(
                    session.getColumnIndexOrThrow(ExcercisesDbAdapter.KEY_PROGRAMS_CONNECTORID));
          //  fillExcercises();
        }
    }
    /*
    private void fillExcercises(){
    	Cursor ExcercisesCursor = mDbHelper.fetchExcercisesForGroup(1); //for now
        startManagingCursor(ExcercisesCursor);
        String[] from = new String[]{ExcercisesDbAdapter.KEY_TITLE};
        int[] to = new int[]{R.id.excercise_name};
        SimpleCursorAdapter adapter = 
        	    new SimpleCursorAdapter(this, R.layout.excercise_list_set_row, ExcercisesCursor, from, to);
        ListView exList = (ListView) findViewById(R.id.ex_list);
        exList.setAdapter(adapter);
    } */
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ExcercisesDbAdapter.KEY_ROWID, mRowId);
        outState.putLong("programs_connector_id", mProgramsConnectorId);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }
    
    private void saveState() {
        String title = mTitleText.getText().toString();
        String desc = mDescText.getText().toString();

        if (mRowId == null || mRowId == 0) {
        	if(title.length() > 0){
        		
        		if(mProgramsConnectorId == null){
        			mProgramsConnectorId = Long.valueOf(0);
        		}
        		long id = mDbHelper.createSession(title, desc, mProgramsConnectorId);
        		Log.i("NEW SESSION CREATED: ", Long.toString(id));
        		if (id > 0) {
        			mRowId = id;
        		}
        	}
        } else {
            mDbHelper.updateSession(mRowId, title, desc, "INPROGRESS");
        }
 
/*        
        if (mProgramId != null && mProgramId > 0){
        	mDbHelper.addSetToProgram(mProgramId, mRowId, mDayNumber);
        	
        	Log.i("Edit Set", "Set added to program with id: " + Long.toString(mProgramId) +
        			" and day_number: " + Long.toString(mDayNumber) +
        			" and set_id: " + Long.toString(mRowId));
        }
 */       
        // adding this set to program (if id provided)
    }
}
