package org.ycalendar.ui;

import org.ycalendar.util.Tuple2;

public class ItemData<E1, E2> extends Tuple2<E1, E2>{
 

    //e1是value e2 显示
    public ItemData(E1 e1, E2 e2) {
		super(e1, e2);
 
	}

	@Override
    public String toString(){
    	   return (e2==null)?"e2:null":e2.toString();
    	}
}
