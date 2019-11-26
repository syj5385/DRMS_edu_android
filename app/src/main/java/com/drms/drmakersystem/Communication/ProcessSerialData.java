package com.drms.drmakersystem.Communication;

/**
 * Created by jjunj on 2017-04-20.
 */

public class ProcessSerialData {

    String[] processed_data = new String[4];

    public ProcessSerialData() {
        super();


    }

    public String[] process_data(String data, int sizeofdata){
        String[] processed_data = new String[4];
        String sentence = data;
        int size = sizeofdata;


        int index;
        int position = 0;

        for(index=1 ; index < sizeofdata-1 ; index++){
            if (sentence.charAt(index) != '^') {
                if(processed_data[position] == null){
                    processed_data[position] = String.valueOf(sentence.charAt(index));
                }
                else {
                    processed_data[position] += sentence.charAt(index);
                }

            }
            else {
                this.processed_data[position] = processed_data[position];
                position ++;
            }
        }

        return processed_data;

    }

    public String getTitle(){
        return processed_data[0];
    }

    public int getSizeParameter(){
        return Integer.parseInt(processed_data[1]);
    }

    public String getLevRound(){
        return processed_data[2];
    }

    public String getParameter(){
        return processed_data[3];
    }
}
