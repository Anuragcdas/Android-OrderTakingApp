package com.example.exe2txt;

public interface RepositoryCallback<T> {

    void onSucess(T result);

    void onError(String errorMessage);
}
