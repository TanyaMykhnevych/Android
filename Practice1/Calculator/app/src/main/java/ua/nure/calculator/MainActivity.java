package ua.nure.calculator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private String _operator = "";
    private Stack<Double> _numbers = new Stack<Double>();
    private TextView _input;
    private boolean _needToClear = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _input = (TextView) findViewById(R.id.inputTv);
    }

    public void clearInput(View view) {
        _input.setText("0");
        _needToClear = false;
        _numbers.clear();
        _operator = "";
    }

    public void onNumberClick(View view) {
        Button number = (Button) view;

        if (_input.getText().toString().equals("0") || _needToClear) {
            _input.setText("");
        }

        _input.append(number.getText());
        _needToClear = false;
    }

    public void onDotClick(View view) {
        if (!_input.getText().toString().contains("."))
            _input.append(".");
    }

    public void onEqualClick(View view){
        _needToClear = true;

        Double number = Double.parseDouble(_input.getText().toString());

        _numbers.push(number);

        Double res = 0.0;
        switch (_operator) {
            case "+":
                res = (_numbers.pop() + _numbers.pop());
                break;
            case "-":
                res = (-_numbers.pop() + _numbers.pop());
                break;
            case "*":
                res = (_numbers.pop() * _numbers.pop());
                break;
            case "/":
                Double divider = _numbers.pop();
                res = _numbers.pop() / divider;
                break;
            default:
                break;
        }
        _input.setText(res.toString());
        _numbers.push(res);

        _operator = "";
    }

    public void onOperatorClick(View view) {
        _needToClear = true;

        Double number = Double.parseDouble(_input.getText().toString());

        _numbers.push(number);

        Button operator = (Button) view;
        String currentOperator = operator.getText().toString();

        if (_operator.isEmpty() || _numbers.size() < 2) {
            _operator = currentOperator;
            return;
        } else {
            Double res = 0.0;
            switch (_operator) {
                case "+":
                    res = (_numbers.pop() + _numbers.pop());
                    break;
                case "-":
                    res = (-_numbers.pop() + _numbers.pop());
                    break;
                case "*":
                    res = (_numbers.pop() * _numbers.pop());
                    break;
                case "/":
                    Double divider = _numbers.pop();
                    res = _numbers.pop() / divider;
                    break;
                default:
                    break;
            }
            _input.setText(res.toString());
            _numbers.push(res);

            _operator = currentOperator;
        }
    }
}
