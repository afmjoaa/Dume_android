package io.dume.dume.student.searchLoading;

public interface SearchLoadingContract {
    interface View {

        void configSearchLoading();

        void initSearchLoading();

        void findView();

    }

    interface Presenter {

        void searchLoadingEnqueue();

        void onSearchLoadingIntracted(android.view.View view);

    }

    interface Model {

        void searchLoadingHawwa();
    }
}
