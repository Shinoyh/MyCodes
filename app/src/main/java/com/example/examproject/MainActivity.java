package com.example.examproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public static boolean flag = false;
    public static BlockButton[][] buttons;
    public static int totalMine = 10;
    public static int totalOpen = 0;
    public static TextView textView;
    public static AlertDialog dialog;
    public static AlertDialog dialog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("WIN, Want play again?")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allStop();
                    }
                })
                .create();

        dialog2 = new AlertDialog.Builder(MainActivity.this)
                .setTitle("LOSE, Want play again?")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        allStop();
                    }
                })
                .create();

        TableLayout table;
        table = (TableLayout) findViewById(R.id.tableLayout);
        TableRow.LayoutParams layoutParams =
                new TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        1.0f);

        textView = findViewById(R.id.textView2);
        textView.setText(""+totalMine);

        buttons = new BlockButton[9][9];
        for (int i = 0; i < 9; i++) {
            TableRow tableRow = new TableRow(this);
            table.addView(tableRow);
            for (int j = 0; j < 9; j++) {
                buttons[i][j] = new BlockButton(this, i, j);
                buttons[i][j].setLayoutParams(layoutParams);
                tableRow.addView(buttons[i][j]);
            }
        }

        for (int i = 0; i < 10; i++) {
            Random r = new Random();
            BlockButton b = buttons[r.nextInt(8)][r.nextInt(8)];
            if (b.isMine) {
                i--;
            } else {
                b.setMine();
            }
        }


        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    flag = true;
                } else {
                    flag = false;
                }
            }
        });

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                BlockButton b = buttons[i][j];
                b.checkNeighbor(i, j);
            }
        }
    }

        public static void openEmptyBlocks (int x, int y){
            if (!flag) {
                BlockButton b = buttons[x][y];
                b.breakBlock();

                if(b.neighborMineCount == 0 && b.checkNeighbor2(x, y)) {
                    for (int i = x - 1; i <= x + 1; i++) {
                        for (int j = y - 1; j <= y + 1; j++) {
                            if (i >= 0 && j >= 0 && i < buttons.length && j < buttons[0].length &&
                                    !buttons[i][j].open_state && !buttons[i][j].isMine && !buttons[i][j].flag_state)
                            {
                                openEmptyBlocks(i, j);
                            }
                        }
                    }
                }
            }
        }
        public void allStop() {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    BlockButton b = buttons[i][j];
                    b.setClickable(false);
                }
            }
        }
    }

class BlockButton extends AppCompatButton {
    int x;
    int y;
    boolean flag_state = false;
    boolean isMine = false;
    int neighborMineCount = 0;
    boolean open_state = false;

    public BlockButton(Context context) {super(context);}

    public BlockButton(Context context, AttributeSet attrs) {super(context, attrs);}

    public BlockButton(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}
    public BlockButton(Context context, int x, int y) {
        super(context);
        this.x = x;
        this.y = y;
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFlag();
                MainActivity.openEmptyBlocks(x, y);
            }
        });
    }
    public boolean isOpen_state() {
        MainActivity.totalOpen++;
        return open_state = true;
    }
    public void toggleFlag() {
        if(MainActivity.flag == true) {
            // 버튼이 클릭되었을 때 수행할 작업을 여기에 추가합니다.
            if (flag_state == false) {
                setText("+");
                MainActivity.totalMine--;
                MainActivity.textView.setText(""+MainActivity.totalMine);
                flag_state = true;
            }
            else if (flag_state == true) {
                setText("");
                MainActivity.totalMine++;
                MainActivity.textView.setText(""+MainActivity.totalMine);
                flag_state = false;
            }
        }
    }
    public boolean setMine() {
        return this.isMine = true;
    }

    public void breakBlock() {
        setClickable(false);
        if(!isMine) {
            setText(""+neighborMineCount);
        }
        isOpen_state();
        if(isMine) {
            setText("x");
            (MainActivity.dialog2).show();
        }
        if(isWin()) {
            (MainActivity.dialog).show();
        }
    }

    public boolean isWin() {
        if (MainActivity.totalOpen == 71) {
            return true;
        }
        return false;
    }
    public int checkNeighbor(int x, int y) {
        this.x = x;
        this.y = y;

        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i >= 0 && j >= 0 && i < 9 && j < 9 && MainActivity.buttons[i][j].isMine) {
                    neighborMineCount++;
                }
            }
        }
        return neighborMineCount;
    }

    public boolean checkNeighbor2(int x, int y) {
        this.x = x;
        this.y = y;

        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (i >= 0 && j >= 0 && i < 9 && j < 9 && MainActivity.buttons[i][j].isMine) {
                    return false;
                }
            }
        }
        return true;
    }
}
