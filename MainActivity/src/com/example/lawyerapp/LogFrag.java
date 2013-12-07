package com.example.lawyerapp;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.AlertDialog;
import android.support.v4.app.ListFragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class LogFrag extends ListFragment {
	
	private DaoInstance daoinstance;
	private SQLiteDatabase db;
	private LogsDao logsDao;
	private Cursor cursor;
	private EditText eText, eHours, eNotes;
	private Button addNewHours, addNewExpense, addNewMileage, deleteLog;
	private Long parentID;
	private String deleteLogStr, doneLogStr;
	private float tempFloat = 0.0f;
	private TextView totalHours, totalExpense, totalMileage;
	private float num_of_hours = 0.0f;
	
	@Override
	public void onCreate(Bundle saved) 
	{
		super.onCreate(saved);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle saved) {
		View v = inflater.inflate(R.layout.main, parent,false); 
		
		// Strings for the delete button
		deleteLogStr = "Delete Log";
        doneLogStr = "Done";
		
        // Get database
		daoinstance = DaoInstance.getInstance(getActivity());
		db = daoinstance.getDb();
		logsDao = daoinstance.getLogsDao();
		
		// Get the CaseID for this set of Logs
		parentID = getActivity().getIntent().getExtras().getLong("id");
		
		// Get the TextViews and set the text for them
		totalHours = (TextView) v.findViewById(R.id.totalHours);
		totalExpense = (TextView) v.findViewById(R.id.totalExpenses);
		totalMileage = (TextView) v.findViewById(R.id.totalMileage);
		
		totalHours.setText("Total Hours: " + calcTotalHours("Hours"));
		totalExpense.setText("Total Expenses: " + calcTotalHours("Expenses"));
		totalMileage.setText("Total Mileage: " + calcTotalHours("Mileage"));
		
		// Get the buttons, set the text and visibility
		addNewHours = (Button) getActivity().findViewById(R.id.buttonNewHours);
		addNewExpense = (Button) getActivity().findViewById(R.id.buttonNewExpense);
		addNewMileage = (Button) getActivity().findViewById(R.id.buttonNewMileage);
		deleteLog = (Button) getActivity().findViewById(R.id.buttonDelete);
		
		resetButtons();
		
		final LayoutInflater lInflater = inflater;

		/*
		 * For the rest of the code, 
		 * Hours = 0
		 * Expenses = 1
		 * Mileage = 2
		 */
		
		// When this button is clicked, pass the value 0 to the onClickMethod function
        addNewHours.setOnClickListener(new View.OnClickListener() {
			
			@Override
			  public void onClick(View view) 
			{
				if (addNewHours.getText() == "New")
				{
					deleteLog.setVisibility(View.VISIBLE);
					deleteLog.setText("Cancel");
					addNewHours.setText("New Hours");
					addNewExpense.setVisibility(View.VISIBLE);
					addNewMileage.setVisibility(View.VISIBLE);
					addNewExpense.setText("New Expense");
					addNewMileage.setText("New Mileage");
				}
				else
				{
					onClickMethod(0, lInflater);
				}
			}		 
		});
        
        // When this button is clicked, pass the value 1 to the onClickMethod function
        addNewExpense.setOnClickListener(new View.OnClickListener() {
			
			@Override
			  public void onClick(View view) 
			{
				onClickMethod(1, lInflater);
			}		 
		});
        
        // When this button is clicked, pass the value 2 to the onClickMethod function
        addNewMileage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			  public void onClick(View view) 
			{
				onClickMethod(2, lInflater);
			}		 
		});
		
        
        deleteLog.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				// If the text was "Delete" when clicked
				if (deleteLog.getText() == deleteLogStr)
				{
					// Set text to "Done"
					deleteLog.setText(doneLogStr);
					
					// Hide other three buttons
					addNewHours.setVisibility(View.INVISIBLE);
					addNewExpense.setVisibility(View.INVISIBLE);
					addNewMileage.setVisibility(View.INVISIBLE);
				}
				else if (deleteLog.getText() == "Cancel")
				{
					resetButtons();
				}
				else
				{
					// Set text to "Delete"
					deleteLog.setText(deleteLogStr);
					
					// Show other three buttons
					addNewHours.setVisibility(View.VISIBLE);
					addNewExpense.setVisibility(View.VISIBLE);
					addNewMileage.setVisibility(View.VISIBLE);
				}
			}
		});
        
        
		String textColumn = LogsDao.Properties.Name.columnName;
		
		// Sort by Date in descending order
		String dateColumn = LogsDao.Properties.Date.columnName;
        String orderBy = dateColumn + " COLLATE LOCALIZED DESC";
		
        // Have the cursor only get logs from the specific case we are in
        cursor = db.query(logsDao.getTablename(), null, "PARENT_ID IN " +
        		"(SELECT PARENT_ID FROM LOGS WHERE PARENT_ID = " + parentID.toString() + ")"
        		, null, null, null, orderBy);
        
        // Choose what values go into which parts of the xml file
        String[] from = {textColumn, LogsDao.Properties.LogType.columnName, LogsDao.Properties.LogDate.columnName};
        int[] to = { R.id.textView1, R.id.textView2, R.id.dateView };

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.list_item2, cursor, from,
                to);
        setListAdapter(adapter);
		
		return v; 
	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) 
	{
		// Get the Logs object that was clicked
		final Logs newlog = logsDao.queryBuilder().where(LogsDao.Properties.Id.eq(id)).unique();
		
		// If you are not deleting logs
		if (deleteLog.getText() == deleteLogStr)
		{	
			final long tempID = id;
			
			int tempType = 0;
			
			// Get the integer based on type
			if (newlog.getLogType().equals("Hours"))
			{
				tempType = 0;
			}
			else if (newlog.getLogType().equals("Expenses"))
			{
				tempType = 1;
			}
			else if (newlog.getLogType().equals("Mileage"))
			{
				tempType = 2;
			}
			
			// Set the integer for later reference
			final int logType = tempType;
			
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			  final View AlertView = View.inflate(getActivity(), R.layout.new_log_dialog, null);
					  
			  	eText = (EditText) AlertView.findViewById(R.id.editTextName);
				eHours = (EditText) AlertView.findViewById(R.id.editTextValue);
				eNotes = (EditText) AlertView.findViewById(R.id.editTextNote);
				
				// Set the name and notes text based on current values
				eText.setText(newlog.getName());
				eNotes.setText(newlog.getNotes());
				
				TextView tempTextView = (TextView) AlertView.findViewById(R.id.textViewName);
				
				// Set the textView and value based on the type of log
				switch (logType)
				{
				case 0:
					eHours.setText(newlog.getHours()+"");
					tempTextView.setText("Hours: ");
					break;
					
				case 1:
					eHours.setText(newlog.getExpenses()+"");
					tempTextView.setText("Expenses: ");
					break;
					
				case 2:
					eHours.setText(newlog.getMileage()+"");
					tempTextView.setText("Mileage: ");
					break;
				}
				
				// If the Notes was an empty string, set the hint
				if (eNotes.getText().toString().isEmpty())
				{
					eNotes.setHint("Enter Note");
				}
				
				// Create the dialog with the current values already in position
			  builder.setView(AlertView);
			  AlertDialog ad = builder.create();
			  ad.setTitle(newlog.getName());
			  ad.setButton(AlertDialog.BUTTON_POSITIVE, "Done",
					    new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) {
					        	
					        	float tempLogFloat;
					        	
					        	// Get the New Name
					        	String noteText = eText.getText().toString();
						        eText.setText("");
						        
						        // Call external function to check the value of the float
						        checkForNull(eHours);
						        tempLogFloat = tempFloat;
						        
						        // Get the New Notes for this log
						        String tempNotes = eNotes.getText().toString();
						        
						        // Create a new Date and format it
						        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
						        String comment = "" + df.format(new Date());
						        
						        String newType = "";
						        
						        // Update the database based on the type of log with the new information
						        switch (logType)
						        {
						        case 0:
						        	newType = "Hours";
						        	
							        Logs log = new Logs(tempID, noteText, parentID, comment, new Date(), tempNotes, newType, tempLogFloat, null, null);
							        logsDao.insertOrReplace(log);
		
							        totalHours.setText("Total Hours: " + calcTotalHours(newType));
							        break;
							        
						        case 1:
						        	newType = "Expenses";
						        	
						        	Logs log1 = new Logs(tempID, noteText, parentID, comment, new Date(), tempNotes, newType, null, null, tempLogFloat);
							        logsDao.insertOrReplace(log1);
		
							        totalExpense.setText("Total Expenses: " + calcTotalHours(newType));
							        break;
						        	
						        case 2:
						        	newType = "Mileage";
						        	
						        	Logs log2 = new Logs(tempID, noteText, parentID, comment, new Date(), tempNotes, newType, null, tempLogFloat, null);
							        logsDao.insertOrReplace(log2);
							        
							        totalMileage.setText("Total Mileage: " + calcTotalHours(newType));
						        	break;
						        }
						        
						        cursor.requery();
					        }
					    });
			  ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
					    new DialogInterface.OnClickListener() 
			  			{
					        public void onClick(DialogInterface dialog, int which) 
					        {
					        	
					        }
					    });
			  
					ad.show();
		}
		else // if the button is set for deleting logs
		{
			// delete the log object
			logsDao.deleteByKey(id);
			
	/*
	 *  Recompute the totals for accuracy
	 */
			if (newlog.getLogType().equals("Hours"))
			{
				totalHours.setText("Total Hours: " + calcTotalHours("Hours"));
			}
			else if (newlog.getLogType().equals("Expenses"))
			{
				totalExpense.setText("Total Expenses: " + calcTotalHours("Expenses"));
			}
			else if (newlog.getLogType().equals("Mileage"))
			{
				totalMileage.setText("Total Mileage: " + calcTotalHours("Mileage"));
			}
		}
		
		cursor.requery();
	}
	
	// Function that checks the input of an EditText
	public void checkForNull(EditText tempText)
	{
		// If no input was provided, default to 0
		if (tempText.getText().toString().isEmpty() && !tempText.getText().equals(null))
        {
        	tempFloat = 0.0f;
        }
        else // If input was provided
        {
        	// Convert the String to a float
	        tempFloat = Float.parseFloat(tempText.getText().toString());
	        tempText.setText("");
        }
	}
	
	// Calculates the total for each of the three types of logs
	public float calcTotalHours(String inputString)
	{
		// Get all logs of a specific type of log
		ArrayList<Logs> newList = (ArrayList<Logs>) logsDao.queryBuilder().where(LogsDao.Properties.ParentID.eq(parentID), LogsDao.Properties.LogType.eq(inputString)).list();
		
		num_of_hours = 0.0f;
		
		// Set the integer based on type
		int type = 0;
		
		if (inputString == "Hours")
		{
			type = 0;
		}
		else if (inputString == "Expenses")
		{
			type = 1;
		}
		else if (inputString == "Mileage")
		{
			type = 2;
		}
		
		// If at least one log was returned
		if (!newList.isEmpty())
		{	
			// Iterate through the list of logs and add up the values based on type
			switch (type)
			{
			case 0:
				for (Logs arrayLogs: newList)
				{
					if (arrayLogs.getHours() != null)
					{
						num_of_hours += arrayLogs.getHours();
					}
				}
				break;
				
			case 1:
				for (Logs arrayLogs: newList)
				{
					if (arrayLogs.getExpenses() != null)
					{
						num_of_hours += arrayLogs.getExpenses();
					}
				}
				break;
				
			case 2:
				for (Logs arrayLogs: newList)
				{
					if (arrayLogs.getMileage() != null)
					{
						num_of_hours += arrayLogs.getMileage();
					}
				}
				break;
			}
		}
		
		// return the total
		return num_of_hours;
	}
	
	// Opens a Dialog based on the type of log being opened
	private void onClickMethod(int logType, LayoutInflater lInflater)
	{
		  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		  
		  final int type = logType;
		  
		  final View AlertView = lInflater.inflate(R.layout.new_log_dialog, null);
		  
		  	eText = (EditText) AlertView.findViewById(R.id.editTextName);
		  	eNotes = (EditText) AlertView.findViewById(R.id.editTextNote);
		  	eHours = (EditText) AlertView.findViewById(R.id.editTextValue);
		
			TextView tempTextView = (TextView) AlertView.findViewById(R.id.textViewName);
			
			tempTextView.setText("");
			
			// Set the hint of the name and Notes
			// Will be the same for all types
			eText.setHint("Name");
			eNotes.setHint("Notes");
			
			// Set the hint based on type
			switch (type)
			{
			case 0:
				eHours.setHint("Hours");
				break;
				
			case 1:
				eHours.setHint("Expenses");
				break;
				
			case 2:
				eHours.setHint("Mileage");
				break;
			}
			
			// Create the new dialog for this Log
		  builder.setView(AlertView);
		  AlertDialog ad = builder.create();
		  ad.setTitle("Create New Log");
		  ad.setButton(AlertDialog.BUTTON_POSITIVE, "Create Log",
				    new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) {
					        
	        	/*
	        	 * Get the values from the user input 
	        	 */
					        float tempLogFloat;
				        	
				        	String noteText = eText.getText().toString();
					        eText.setText("");
					        
					        checkForNull(eHours);
					        tempLogFloat = tempFloat;
					        
					        String tempNotes = eNotes.getText().toString();
					        
					        final DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
					        String comment = "" + df.format(new Date());
					        
					        String newType = "";
					        
		        /*
		         * Insert the log based on type
		         */
					        switch (type)
					        {
					        case 0:
					        	newType = "Hours";
					        	
						        Logs log = new Logs(null, noteText, parentID, comment, new Date(), tempNotes, newType, tempLogFloat, null, null);
						        logsDao.insert(log);
	
						        totalHours.setText("Total Hours: " + calcTotalHours(newType));
						        break;
						        
					        case 1:
					        	newType = "Expenses";
					        	
					        	Logs log1 = new Logs(null, noteText, parentID, comment, new Date(), tempNotes, newType, null, null, tempLogFloat);
						        logsDao.insert(log1);
	
						        totalExpense.setText("Total Expenses: " + calcTotalHours(newType));
						        break;
					        	
					        case 2:
					        	newType = "Mileage";
					        	
					        	Logs log2 = new Logs(null, noteText, parentID, comment, new Date(), tempNotes, newType, null, tempLogFloat, null);
						        logsDao.insert(log2);
						        
						        totalMileage.setText("Total Mileage: " + calcTotalHours(newType));
					        	break;
					        }
					        
					        cursor.requery();
				        }
				    });
		  ad.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
				    new DialogInterface.OnClickListener() 
		  			{
				        public void onClick(DialogInterface dialog, int which) 
				        {
				        	
				        }
				    });
		  
				ad.show();
	  }
	
	public void resetButtons()
	{
		addNewHours.setVisibility(View.VISIBLE);
		deleteLog.setVisibility(View.VISIBLE);
		deleteLog.setText(deleteLogStr);
		addNewHours.setText("New");
		addNewExpense.setVisibility(View.INVISIBLE);
		addNewMileage.setVisibility(View.INVISIBLE);
		addNewExpense.setText("New Expense");
		addNewMileage.setText("New Mileage");
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		resetButtons();
	}
}