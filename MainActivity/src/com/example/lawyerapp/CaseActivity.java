package com.example.lawyerapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class CaseActivity extends FragmentActivity {

	// All of the buttons displayed on the page of each case
	private Button mDocsBut;
	private Button mContactBut;
	private Button mTimeBut;
	private Button mTitle;
	private Button newHoursButton, newExpenseButton, newMileageButton, deleteButton;
	
	// Fragments that switch out when you flip between tabs
	// This changes what you see displayed in the middle of the screen while leaving things like the case name untouched at the top
	private Fragment contactfrag; //1
	private Fragment filefrag;    //2
	private Fragment logfrag;     //3
	private int FragSelect=0;     // Selector for the current fragment on display
	
	// a variable to store the current case's ID
	private Long caseID;  
	// lets us use GreenDao to save and retrieve data from the database created on the device
	private CasesDao casesDao;
	private SQLiteDatabase db;
    private DaoInstance daoinstance;
    
    public ProgressBar spinner;

    //on Create called when page is first loaded
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //gets the spot to place our fragments to switch out with tabs
        setContentView(R.layout.fragment_record);
        
        //gets our specific instance of GreenDao things
        daoinstance = DaoInstance.getInstance(this);
        db = daoinstance.getDb();
        casesDao = daoinstance.getCaseDao();
        
        //find all of our objects on the layout file to use in java
        newHoursButton=(Button) findViewById(R.id.buttonNewHours);
        newExpenseButton=(Button) findViewById(R.id.buttonNewExpense);
        newMileageButton=(Button) findViewById(R.id.buttonNewMileage);
		deleteButton=(Button) findViewById(R.id.buttonDelete);
        
		// Get the name and id of the case
		// This is passed by intent from MainActivity to this activity
        String newName = getIntent().getExtras().getString("name");
        caseID = getIntent().getExtras().getLong("id");
		
		mTitle = (Button) findViewById(R.id.Title);
		
		mTitle.setText(newName);
		
		//Defines what to do when you click on the Case Title
		//Creates a pop up window using a dialog allowing you to modify the case's name
		mTitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(CaseActivity.this);
				  final LayoutInflater inflater = getLayoutInflater();

				  final View AlertView = inflater.inflate(R.layout.new_case_dialog, null);
				  
				  // Don't show this EditText from the new_case_dialog.xml format
				  final EditText caseType = (EditText) AlertView.findViewById(R.id.eTextType);
				  caseType.setVisibility(View.GONE);
				  
				  // Set the text of the EditText to the current case name
				  final EditText caseName = (EditText) AlertView.findViewById(R.id.eTextNote);
		        	Cases tempCase = casesDao.queryBuilder().where(CasesDao.Properties.Id.eq(caseID)).unique();
		        	caseName.setText(tempCase.getName());
				  
		        	// Create the Dialog pop-up
				  builder.setView(AlertView);
				  AlertDialog ad = builder.create();
				  ad.setTitle("Rename Case");
				  ad.setButton(AlertDialog.BUTTON_POSITIVE, "Rename",
						    new DialogInterface.OnClickListener() {
						        public void onClick(DialogInterface dialog, int which) {
						        	
						        	// Get the case from the database
						        	Cases tempCase = casesDao.queryBuilder().where(CasesDao.Properties.Id.eq(caseID)).unique();
						        	
						        	// Get the new name
						        	String newName = caseName.getText().toString();
						        	
						        	// Create a new case with identical data, but the new name
						        	Cases newCase = new Cases(caseID, newName, tempCase.getCasetype(), tempCase.getCaseDate(), tempCase.getDate());
						        	
						        	// Replace the old case with the new case
						        	casesDao.insertOrReplace(newCase);
						        	
						        	// Set the Text in the Title of this screen
						        	mTitle.setText(newName);
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
			
		});
		
		
		mDocsBut=(Button) findViewById(R.id.recentDocs);
		mContactBut=(Button) findViewById(R.id.recentContacts);
		mTimeBut=(Button) findViewById(R.id.recentTimeLogs);
		
		
		//initializes our three types of fragments that can be place in the middle of the screen
		contactfrag = new ContactFrag();
		filefrag = new FileFrag();
		logfrag = new LogFrag();
		final android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
		
		//Set colors for some of the objects in the xml layout file
		final int buttonColor, buttonSelected;
		
		buttonColor = getResources().getColor(R.color.colorScheme2);
		buttonSelected = getResources().getColor(R.color.tabSelected);
		
		newHoursButton.setBackgroundColor(buttonColor);
		newExpenseButton.setBackgroundColor(buttonColor);
		newMileageButton.setBackgroundColor(buttonColor);
		deleteButton.setBackgroundColor(buttonColor);
		
		spinner = (ProgressBar) findViewById(R.id.spinner);
		spinner.setVisibility(View.GONE);
		
		/* The next three functions are all used to handle the switching out of the fragments and other related items such as buttons at the bottom of the screen
		 * They are each on click button listeners that check to see if the current button just got pressed 
		 * If a button is pressed it changes color and switches out the current fragment for the newly selected one
		 * There are checks to make sure you can't place a new fragment if one already exist because it causes an error, i.e. you can't overlap fragments
		 * There are bundles used to pass relevant information like case ID to each fragment that gets loaded as well
		 */
						
		mDocsBut.setOnClickListener(new View.OnClickListener() {

			  @Override
			  public void onClick(View view) {
				  mDocsBut.setBackgroundColor(buttonSelected);
				  mContactBut.setBackgroundColor(buttonColor);
				  mTimeBut.setBackgroundColor(buttonColor);
				  
					if (FragSelect==0)
					{
						FragmentTransaction fta = fm.beginTransaction();
						Bundle bundle = new Bundle();
						bundle.putLong("id", caseID);
						filefrag.setArguments(bundle);
						fta.add(R.id.tabfrag, filefrag);
						fta.commit(); 
						FragSelect=2;
					}
					
					
					if (FragSelect != 2) {
						if(FragSelect==1)
						{
							FragmentTransaction fta = fm.beginTransaction();
							fta.remove(contactfrag).commit();
						}
						else  //3
						{
							FragmentTransaction fta = fm.beginTransaction();
							fta.remove(logfrag).commit();
						}
						FragmentTransaction fta = fm.beginTransaction();
						Bundle bundle = new Bundle();
						bundle.putLong("id", caseID);
						filefrag.setArguments(bundle);
						fta.add(R.id.tabfrag, filefrag);
						fta.commit(); 
						FragSelect=2;
					}
			  }

			});
		
		mContactBut.setOnClickListener(new View.OnClickListener() {

			  @Override
			  public void onClick(View view) {
				  mDocsBut.setBackgroundColor(buttonColor);
				  mContactBut.setBackgroundColor(buttonSelected);
				  mTimeBut.setBackgroundColor(buttonColor);
				  
				  spinner.setVisibility(View.VISIBLE);
				  
				  if (FragSelect==0)
					{
						FragmentTransaction fta = fm.beginTransaction();
						Bundle bundle = new Bundle();
						bundle.putLong("id", caseID);
						contactfrag.setArguments(bundle);
						fta.add(R.id.tabfrag, contactfrag);
						fta.commit(); 
						FragSelect=1;
					}
					
					
					if (FragSelect != 1) {
						if(FragSelect==2)
						{
							FragmentTransaction fta = fm.beginTransaction();
							fta.remove(filefrag).commit();
						}
						else  //3
						{
							FragmentTransaction fta = fm.beginTransaction();
							fta.remove(logfrag).commit();
						}
						FragmentTransaction fta = fm.beginTransaction();
						Bundle bundle = new Bundle();
						bundle.putLong("id", caseID);
						contactfrag.setArguments(bundle);
						fta.add(R.id.tabfrag, contactfrag);
						fta.commit(); 
						FragSelect=1;
					}
			  }

			});
		mTimeBut.setOnClickListener(new View.OnClickListener() {

			  @Override
			  public void onClick(View view) {
				  mDocsBut.setBackgroundColor(buttonColor);
				  mContactBut.setBackgroundColor(buttonColor);
				  mTimeBut.setBackgroundColor(buttonSelected);
					
				  if (FragSelect==0)
					{
						FragmentTransaction fta = fm.beginTransaction();
						Bundle bundle = new Bundle();
						bundle.putLong("id", caseID);
						logfrag.setArguments(bundle);
						fta.add(R.id.tabfrag, logfrag);
						fta.commit(); 
						FragSelect=3;
					}
					
					
					if (FragSelect != 3) {
						if(FragSelect==1)
						{
							FragmentTransaction fta = fm.beginTransaction();
							fta.remove(contactfrag).commit();
						}
						else  //2
						{
							FragmentTransaction fta = fm.beginTransaction();
							fta.remove(filefrag).commit();
						}
						
						FragmentTransaction fta = fm.beginTransaction();
						Bundle bundle = new Bundle();
						bundle.putLong("id", caseID);
						logfrag.setArguments(bundle);
						fta.add(R.id.tabfrag, logfrag);
						fta.commit(); 
						FragSelect=3;
					}
			  }
		});
    }  
	//on resume is called when returning to this page, removes all the buttons at the bottom of the screen since there is not a tab selected yet
	@Override
	public void onResume()
	{
		super.onResume();
		
		newHoursButton.setVisibility(View.INVISIBLE);
		newExpenseButton.setVisibility(View.INVISIBLE);
		newMileageButton.setVisibility(View.INVISIBLE);
		deleteButton.setVisibility(View.INVISIBLE);
	}
}