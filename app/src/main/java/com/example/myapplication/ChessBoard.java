package com.example.myapplication;

import android.view.View;
import android.widget.ImageView;

public class ChessBoard{


    public String MainBoard[][];
    public char ch= 'A';

    ImageView imageView[][];


  public ChessBoard(View view){
        init(view);

    }

    private void init(View view){

      //System.out.println("으갹갹 : " + (Long.parseLong(ch, 16)));
        // 보드 초기화
        MainBoard = new String[8][8];
        imageView = new ImageView[8][8];

        for(int i=0; i<8; i++){
            for(int j=0; j<8; j++){
                //MainBoard[i][j] = ( String.valueOf(ch+j) + Integer.toString(8-i));
                MainBoard[i][j] = ( Character.toString((char)(ch+j)) + Integer.toString(8-i));
                System.out.println("각 배열의 들어간 값 MainBoard[" + i + "]" + "[" + j + "] : " + MainBoard[i][j] );

                // R.id.? 을 배열로 하는 함수법
                int k = view.getResources().getIdentifier(MainBoard[i][j],"id","com.example.myapplication");
                imageView[i][j] = (ImageView)view.findViewById(k);

                // 폰 위치
                if(i==1 || i==6){
                    if(i==1)
                        imageView[i][j].setImageResource(R.drawable.ic_pawn);
                    else
                        imageView[i][j].setImageResource(R.drawable.ic_pawn2);
                }

                // 폰 제외 나머지 말 위치
                if(i==0 || i==7){
                    if(i==0) {
                        // 룩 위치
                        if (j == 0 || j == 7) {
                            imageView[i][j].setImageResource(R.drawable.ic_rook);
                        }
                        // 나이트 위치
                        else if(j == 1 || j == 6){
                            imageView[i][j].setImageResource(R.drawable.ic_knight);
                        }
                        // 비숍 위치
                        else if(j == 2 || j == 5){
                            imageView[i][j].setImageResource(R.drawable.ic_bishop);
                        }
                        // 퀸 위치
                        else if(j==3){
                            imageView[i][j].setImageResource(R.drawable.ic_queen);
                        }
                        // 킹 위치
                        else if(j==4){
                            imageView[i][j].setImageResource(R.drawable.ic_king);
                        }
                    }

                    else if(i==7){
                        // 룩 위치
                        if (j == 0 || j == 7) {
                            imageView[i][j].setImageResource(R.drawable.ic_rook2);
                        }
                        // 나이트 위치
                        else if(j == 1 || j == 6){
                            imageView[i][j].setImageResource(R.drawable.ic_knight2);
                        }
                        // 비숍 위치
                        else if(j == 2 || j == 5){
                            imageView[i][j].setImageResource(R.drawable.ic_bishop2);
                        }
                        // 퀸 위치
                        else if(j==3){
                            imageView[i][j].setImageResource(R.drawable.ic_queen2);
                        }
                        // 킹 위치
                        else if(j==4){
                            imageView[i][j].setImageResource(R.drawable.ic_king2);
                        }
                    }
                }



            }
        }





    }
}
