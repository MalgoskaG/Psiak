package com.example.android.psiak.ui.main;

import com.example.android.psiak.data.network.Repository;
import com.example.android.psiak.data.network.SortingStrategyFactory;
import com.example.android.psiak.model.Animal;
import com.example.android.psiak.model.AnimalType;
import com.example.android.psiak.model.DogFirebase;
import com.example.android.psiak.ui.base.BasePresenter;
import com.google.firebase.database.DatabaseException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import io.realm.RealmResults;
import timber.log.Timber;

class MainPresenter
        extends BasePresenter<MainContract.View>
        implements MainContract.Presenter<MainContract.View>, FirebaseDataListener {

    Repository.Firebase firebaseRepository;
    Repository.LocalRepository localRepository;

    /**
     * Initialize FirebasePresenter with Firebase repository. After initialization FirebasePresenter object will be set as a
     * data listener object for callback from Firebase repository
     *
     * @param repository      Repository.Firebase object that will be used with this Presenter
     * @param localRepository Repository.LocalRepository object that will be used with this Presenter
     */

    public MainPresenter(Repository.Firebase repository, Repository.LocalRepository localRepository) {
        this.firebaseRepository = repository;
        this.localRepository = localRepository;
        this.firebaseRepository.setDataListner(this);
    }

    //region old methods from Presenter interface
    @Override
    public void getAllDogs() {
        ArrayList<DogFirebase> dogsData = firebaseRepository.getCachedDogs();

        if (dogsData.size() > 0 && isViewAttached()) {
            setDogsWithoutDuplicates(dogsData);
        } else {
            firebaseRepository.getAllObjects();
        }
    }

    @Override
    public void getSortedDogs(String fieldName) {
        ArrayList<DogFirebase> dogsData = firebaseRepository.getCachedDogs();
        Collections.sort(dogsData, SortingStrategyFactory.getStrategyForField(fieldName));
        if (isViewAttached()) {
            setDogsWithoutDuplicates(dogsData);
        }
    }

    @Override
    public void addNewFavouriteDog(DogFirebase dogFirebase) {

        dogFirebase.setFavourite(true);
        localRepository.add(dogFirebase);

        Timber.d("Save " + dogFirebase.getName());

        RealmResults<DogFirebase> dogs = localRepository.getAll();

        for (DogFirebase d : dogs) {
            Timber.d(d.toString());
        }
    }

    //endregion

    @Override
    public void setDogsData(ArrayList<DogFirebase> dogsData) {
        if (isViewAttached()) {
            setDogsWithoutDuplicates(dogsData);
        }
    }

    @Override
    public void setDogsWithoutDuplicates(ArrayList<DogFirebase> dogsData) {
        Iterator<DogFirebase> dogsToModify;
        List<DogFirebase> dogsToShow = new ArrayList<DogFirebase>();
        for (dogsToModify = dogsData.iterator(); dogsToModify.hasNext(); ) {
            DogFirebase dogFromNetwork = dogsToModify.next();
            if (localRepository.checkIfEmpty(dogFromNetwork.getId())) {
                dogsToShow.add(dogFromNetwork);
            }
        }
        view.showAllDogs(dogsToShow);
    }


    //region new methods from Presenter interface
    @Override
    public void getAnimalsForShelter(AnimalType animalType, String shelterName) {
        //TODO implment filtering by shelter
    }

    @Override
    public void getAllAnimals(AnimalType animalType) {

        switch (animalType) {
            case DOGS:
                getAllDogs();
                break;
            case CATS:
                //TODO implement getting all cats
                break;
            case OTHERS:
                //TODO: implement getting all others
                break;
        }
    }

    @Override
    public void addNewFavouriteAnimal(Animal animal) {

    }

    @Override
    public void getAllAnimalsForShelter(String shelterName) {

    }
    //endregion


    @Override
    public void setErrorMessage(DatabaseException databaseException) {
        view.showErrorMessage(databaseException.getMessage());
    }


}
