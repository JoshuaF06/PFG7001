package com.android.pfg7001.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.android.pfg7001.R;

public class ConnectionDialog extends DialogFragment {

    private IConnectionDialog myInterface;

    public interface IConnectionDialog {
        void openSettings();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof IConnectionDialog) {
            this.myInterface = (IConnectionDialog) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.TemaDialogoAlerta);

        builder.setTitle("Alerta");
        builder.setMessage("Para usar esta aplicación es necesario estar conectado a la red Wifi PFG7001\nDesea abrir la configuración de Wifi?");
        builder.setPositiveButton("Si", (dialog, which) -> {
            myInterface.openSettings();
            dialog.cancel();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        return builder.create();
    }

}
