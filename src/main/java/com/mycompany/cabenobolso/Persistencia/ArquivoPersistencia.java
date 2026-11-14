package com.mycompany.cabenobolso.persistencia;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.mycompany.cabenobolso.modelo.Planejamento;

public class ArquivoPersistencia
        implements Persistencia {

    private static final String ARQUIVO =
            "planejamento.dat";

    @Override
    public void salvar(Planejamento planejamento) {

        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(ARQUIVO))) {

            out.writeObject(planejamento);

        } catch (IOException e) {

            System.out.println(
                "Erro ao salvar: "
                + e.getMessage()
            );
        }
    }

    @Override
    public Planejamento carregar() {

        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(ARQUIVO))) {

            return (Planejamento) in.readObject();

        } catch (FileNotFoundException e) {

            System.out.println(
                "Arquivo não encontrado."
            );

        } catch (IOException e) {

            System.out.println(
                "Erro ao ler o arquivo."
            );

        } catch (ClassNotFoundException e) {

            System.out.println(
                "Classe inválida."
            );
        }

        return null;
    }
}