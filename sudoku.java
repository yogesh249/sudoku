import java.awt.*;
import java.awt.event.*;

import java.text.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**************************************************************************/
public class sudoku extends JFrame implements ActionListener
{
	JTable table=null;

	static Object data[][]=new Object[9][9];
	Object cols[]=new Object[9];
	int NumOfTabs = -1;
	int /*nonzero=0,*/nonzero1=0;
	HashMap<Integer,Set<Integer>> mp = new HashMap<Integer,Set<Integer>>();
	DefaultTableModel model=null;
	static final java.util.List<Integer> test=new ArrayList<Integer>(9);
	
	static
	{
		for(int i=1;i<=9;i++)
			test.add(i);

	}
	/**********************************************************************/
	sudoku(String s)
	{
		super(s);
		initMyData();
		model=new DefaultTableModel(data,cols);
		table=new JTable(model);
		table.setAutoCreateColumnsFromModel(true);
		
		JButton calculate=new JButton("Calculate");
		JButton clear=new JButton("Clear");
		calculate.addActionListener(this);
		clear.addActionListener(this);
		JPanel lp = new JPanel();
		setResizable(false);
		
		lp.setLayout(new BorderLayout());
		lp.add(table,BorderLayout.CENTER);
		lp.add(calculate,BorderLayout.NORTH);
		lp.add(clear,BorderLayout.SOUTH);
		getContentPane().add(lp);
	    
	}
	/**********************************************************************/
	public java.util.List<Integer> row(int n)
	{
		java.util.List<Integer> r=new ArrayList<Integer>(9);
		for(int i=0;i<=8;i++)
			try
			{
				r.add(Integer.parseInt(data[n][i].toString()));
			}catch(NumberFormatException e)
			{
				r.add(0);
			}

		return r;
	}
	/**********************************************************************/
	public java.util.List<Integer> column(int n)
	{
		java.util.List<Integer> c=new ArrayList<Integer>(9);
		for(int j=0;j<=8;j++)
		try
		{
			c.add(Integer.parseInt(data[j][n].toString()) );
		}
		catch(NumberFormatException e)
		{
			c.add(0);
		}
		return c;
	}
	/**********************************************************************/
        /*
         * This method returns a List having 3x3 matrix
         * that starts at point indices, x,y
         */
	public java.util.List<Integer> mat3x3(int x, int y)
	{
		java.util.List<Integer> mat3x3=new ArrayList<Integer>(9);
		for(int i=x;i<=x+2;i++)
			for(int j=y;j<=y+2;j++)
				try
				{
					mat3x3.add(Integer.parseInt(data[i][j].toString()));
				}
				catch(NumberFormatException e)
				{
					mat3x3.add(0);
				}
		return mat3x3;
	}
	/**********************************************************************/
        /*
         * This is to intialize sample data
         */
	final void initMyData()
	{
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				data[i][j]="";
		data[0][1]=4;
		data[0][4]=9;
		data[1][1]=9;
		data[1][4]=7;
		data[1][5]=2;
		data[2][2]=7;
		data[2][7]=2;
		data[3][0]=5;
		data[3][3]=3;
		data[3][6]=8;
		data[4][0]=3;
		data[4][8]=1;
		data[5][2]=6;
		data[5][5]=8;
		data[5][8]=3;
		data[6][1]=8;
		data[6][6]=6;
		data[7][3]=6;
		data[7][4]=1;
		data[7][7]=9;
		data[8][4]=2;
		data[8][7]=4;
	}
	/**********************************************************************/
	sudoku()
	{
	}
	/**********************************************************************/
        @Override
	public void actionPerformed(ActionEvent arg0)
	{

//		nonzero=0;
		String str=(String)arg0.getActionCommand();
		if(str.equalsIgnoreCase("Calculate"))
		{

			Date date = new Date();
			System.out.print("\nTime = " + date);
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
				{
					try
					{
						data[i][j]=table.getValueAt(i,j);
						if(data[i][j].equals("")) data[i][j]=0;
					}
					catch(Exception e)
					{
						data[i][j]=0;
					}
				}
			solve();
			int nonZeroElements = countNonZero(data);
			//Back Luck !! No solution
			if(nonZeroElements<81)
			{
				System.out.print("\nUnable to find a solution " + nonZeroElements);
                                JOptionPane.showMessageDialog(null, "No solution found");
				print();
			}
			date = new Date();
			DateFormat df;
			df=DateFormat.getDateInstance(DateFormat.LONG, Locale.UK);
			try
			{
				WriteIntoFile(df.format(date) + ".txt",table);
			}
			catch(IOException ex)
			{
				System.out.println("An IO Exception occured");
			}
			date = new Date();
			System.out.print("\nTime = " + date);
		}
		//Now if the source is clear then, clear all the boxes
		if(str.equalsIgnoreCase("Clear"))
		{
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
				{
					data[i][j]="";
					model.setValueAt(data[i][j],i,j);
//					nonzero=0;
				}
			table.repaint();
		}
	}
	/**********************************************************************/
	public int GeneratePossibleValues(HashMap<Integer,Set<Integer>> mp)
	{
			mp.clear();
			for(int i=0;i<9;i++)
				for(int j=0;j<9;j++)
					if(data[i][j].equals(0) )
					{
						Set<Integer> val = new HashSet<Integer>();
						mp.put(i*10+j,val);
					}
			for(int i =0; i<=8;i++)
				for(int j=0;j<=8;j++)
					if(data[i][j].equals(0) )
					{
						for(int k=1;k<=9;k++)
							if(canbfilled(k,i,j))
							{
								Set<Integer> oldValue = new HashSet<Integer>();
								oldValue = (Set<Integer>)mp.get(i*10+j);
								oldValue.add(k);
								mp.put(i*10+j,oldValue);
							}
						if(((Set)mp.get(i*10+j)).isEmpty())
							return -1;
					}
			return 0;
	}
	/**********************************************************************/
        public static void arraycopy(Object[][] aSource, Object[][] aDestination) {
            for (int i = 0; i < aSource.length; i++) {
                System.arraycopy(aSource[i], 0, aDestination[i], 0, aSource[i].length);
            }
        }
	public void solve()
	{
		Object tempdata[][] = new Object[9][9];
		//Needs to take the backup...
                arraycopy(data, tempdata);

		//Fill Elements that can be uniquely identified.
		FillUnique();
                int n2zero = countNonZero(data);
		if(n2zero>=81)
			return;
		//After calling Fillunique, GeneratePossibleValues, there may be some situation like dead end.
		//GeneratePossibleValues returns 0 on success, means, everycell has atleast one option.
		if(GeneratePossibleValues(mp)!=0)
			return;
		//This will give -1 when we reach dead  end.....
		int cell = findCellWithMinimumOptions(mp);
		if(cell==-1)
		{
			//if we reach dead end then revert back to original data and return i.e. backtrack...
                        arraycopy(tempdata, data);
        		return;
		}
		//flow reaches here, means, there exists a cell that has 2 or more options.
		Set<Integer> possibleValues = (Set<Integer>)mp.get(cell);
		Iterator<Integer> iter = possibleValues.iterator();
		int row = cell/10;
		int col = cell%10;
		//For each option we need to repeat the process....
		while(iter.hasNext())
		{
			int value=-1;
			value = (Integer)iter.next();
			data[row][col]=value;
			System.out.print(".");
			solve();
			int n3Zero = countNonZero(data);
			if(n3Zero>=81) return;
			//Not successful, restore original data and try with the next option.
                        arraycopy(tempdata, data);

		}
	}
	/**********************************************************************/
	public static int findCellWithMinimumOptions(HashMap<Integer, Set<Integer>> m)
	{
		int returnVal=9;
		int returnKey = -1;
		Set<Integer> keySet = m.keySet();
		Iterator<Integer> iter = keySet.iterator();
		while(iter.hasNext())
		{
			int key = (Integer)iter.next();
			int size = -1;
			try
			{
				size = ((Set<Integer>)m.get(key)).size();
			}
			catch(Exception e)
			{
				System.out.print("\n\nException caught : " + e +"\n");
			}
			if(size==0) return -1;

			if(returnVal>size && size>=2)
			{
				returnVal = size;
				returnKey = key;
			}
		}
		return returnKey;
	}
	/**********************************************************************/
	public void FillUnique()
	{
		int n6zero = countNonZero(data);
		nonzero1=n6zero;
		while(true)
		{
			FillOnlyPossibleElements();
			FillUniquePositionsIn3x3();
			FillUniquePositionsInRows();
			FillUniquePositionsInCols();
                        int n7zero = countNonZero(data);
			//this condition is true when no further progress can be made.
			//countNonZero();
			if(n7zero==nonzero1) break;//Further progress is not possible using unique elements
			else nonzero1=n7zero;//Further progress is possible.

			if(n7zero>=81)
			{
				System.out.println("\nSolution achieved");
				for(int i=0;i<9;i++)
					for(int j=0;j<9;j++)
						model.setValueAt(data[i][j],i,j);
				table.repaint();
				//JOptionPane.showMessageDialog(new JPanel(), "Solution achieved");
				return;
			}

		}

	}
	/**********************************************************************/
	public static void main(String[] args)
	{
		sudoku frame=new sudoku("Made by : Yogesh Gandhi");
		frame.setSize(300,230);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
	/**********************************************************************/
	public void WriteIntoFile(String filename, JTable tb) throws IOException
	{
		OutputStream f1 = new FileOutputStream(filename);

		String output="";

		for(int i =0 ;i<9; i++)
		{
			for(int j =0 ; j< 9; j++)
			{
				output = output + data[i][j].toString()+(j%3==2 ? "\t" : " " );
			}
			output = output + (i%3==2 ? "\n\n" : "\n");

		}
		byte buf[]=output.getBytes();

		f1.write(buf);
		f1.close();

	}
	/**********************************************************************/
	public void reset()
	{
		test.clear();
		for(int i=1;i<=9;i++)
			test.add(i);

	}
	/**********************************************************************/
	int find_unique_elements(int i,int j)
	{
		int si[]=new int[1];//Actually these are variables I need to pass by
		int sj[]=new int[1];//reference, that is why they are created as arrays
		reset();//resets the test Vector

		//This function finds the top left point coordinates
		//of 3 X 3 matrix in which the point resides
		findmatstartpoint(si,sj,i,j);

		for(int a=1;a<=9;a++)
		//This loop removes the elements in
		//row no. i and column no. j and in 3x3 matrix from the test array
		{
			if(row(i).contains(a) || column(j).contains(a)
				|| mat3x3(si[0],sj[0]).contains(a))
			{
				test.remove(Integer.valueOf(a));
			}
		}
		return(test.size());
	}
	/**********************************************************************/
	void findmatstartpoint(int s1[], int s2[],int i,int j)
	{
		s1[0] = (i/3)*3; //getting the nearest multiple of 3 <=i
		s2[0] = (j/3)*3; //getting the nearest muliple of 3 <=j
	}
	/**********************************************************************/
	/*int findmin_in_test()
	{
		//This function will return the first nonzero element if available in test
		if(test.size()==1)
			return test.firstElement();
		else
			return 0;

	}*/
	/**********************************************************************/
	void inc(int i[],int j[])
	{
		//This function will find the next zero or "" element after (i,j)
		do
		{
			j[0]++;
			if(j[0]==9)
			{
				j[0]=0;
				i[0]++;
			}
			if(i[0]==9)	return;
		}while(data[i[0]][j[0]].equals(0)==false);//data[i[0]][j[0]].equals(" ")==false && );

	}
	/**********************************************************************/
	void print()
	{
		int i,j;
		System.out.print("\n==================================================\n");
		for(i=0;i<=8;i++)
		{
			if(i%3==0 && i!=0)
				System.out.println();
			for(j=0;j<=8;j++)
				try
				{
					if(j%3==2)
						System.out.print(data[i][j]+"\t");
					else
						System.out.print(data[i][j]+" ");
				}
				catch(Exception e)
				{
					if(j%3==2)
						System.out.print("\t");
					else
						System.out.print(" ");
				}

			System.out.println();
		}
		int n4zero = countNonZero(data);
		System.out.print("\n\n");
		System.out.print("Nonzero items = " + n4zero);

		System.out.print("\n==================================================\n");


	}
	/**********************************************************************/
	int no_of_positions_in_3x3_matrix(int a,int si,int sj)
	{
		int i=0,j=0,nopos=0;
		int ii=0,jj=0;
		for(i=si;i<=si+2;i++)
			for(j=sj;j<=sj+2;j++)
				if(canbfilled(a,i,j))
				{
						ii=i;
						jj=j;//Store the last possible position of a
						nopos++;
						if(nopos>=2) return nopos;
				}
		if(nopos==1)
			data[ii][jj]=a;
		return nopos;
	}
	/**********************************************************************/
	boolean canbfilled(int a,int x,int y)
	{
		int si[]=new int[1];
		int sj[]=new int[1];
		if(data[x][y].equals(0)==false)// && data[x][y].equals(" ")==false)
			return false;
		findmatstartpoint(si,sj,x,y);

		if(row(x).contains(a)
		|| column(y).contains(a)
		|| mat3x3(si[0],sj[0]).contains(a))
				return false;

		return true;
	}
	/**********************************************************************/
	/* This function finds the candidate positions, where only one number can be filled*/
	/* for e.g. at position (2,3) only 1 can be filled.*/
	/* So, it will fill 1 at position 2,3.*/
	void FillOnlyPossibleElements()
	{
		int i[]={0};
		int j[]={-1};

		while(true)
		{
			inc(i,j);//here i and j gets changed, that is why created as arrays.
			if(8<i[0])	break;
			if(find_unique_elements(i[0],j[0])==1)
			{

				//find nonzero element in test and assign it to data
				data[i[0]][j[0]]=test.get(0);//findmin_in_test();
				i[0]=0;
				j[0]=-1;//resets the value of i and j
//				nonzero++;
				reset();
			}
		}


	}
	/**********************************************************************/
	/*This function will find the elements that can be filled only at
	some unique position in 3x3 matrix and fills it there */
        void FillUniquePositionsIn3x3()
	{
		int i,j,k;
		for(i=0;i<=6;i+=3)
                    for(j=0;j<=6;j+=3)
                            for(k=1;k<=9;k++)
                                if(no_of_positions_in_3x3_matrix(k,i,j)==1)
                                {
                                    // well we need not do
                                    // anything here
                                    // if the no. of positions 
                                    // for k, in 3x3 matrix startig with i,j is 1
                                    // then it has already been filled in data array
                                    // by the function no_of_positions_in_3x3_matrix.
                                }
	}
	/**********************************************************************/
	void FillUniquePositionsInRows()
	{
		int i=0,j=0,k=0,nopos=0;
		int ii=0,jj=0;
		for(k=1;k<=9;k++)
		{
                    for(i=0;i<=8;i++)
                    {
                        nopos=0;
                        for(j=0;j<=8;j++)
                            if(canbfilled(k,i,j))
                            {
                                    ii=i;jj=j;
                                    nopos++;
                            }
                    }
                    if(nopos==1)
                    {
                             data[ii][jj]=k;
                    }

		}
	}
	/**********************************************************************/
	void FillUniquePositionsInCols()
	{
		int i=0,j=0,k=0,nopos=0;
		int ii=0,jj=0;
		for(k=1;k<=9;k++)
		{
			for(j=0;j<=8;j++)
			{
                            nopos=0;
                            for(i=0;i<=8;i++)
                                if(canbfilled(k,i,j))
                                {
                                        ii=i;jj=j;
                                        nopos++;
                                }
			}
                        if(nopos==1)
                        {
                                 data[ii][jj]=k;
                        }                        
		}
	}
	/***********************************************************************/
	public int countNonZero(Object x[][])
	{
		int nnzero=0;
		for(int i =0;i<9;i++)
		{
			for(int j=0;j<9;j++)
			{
				try
				{
					if(x[i][j].equals(0)==false && x[i][j].equals("")==false)
						nnzero++;
				}
				catch(Exception e)
				{}
			}
		}
                return nnzero;
	}//ends countNonZero
	/***********************************************************************/
}
