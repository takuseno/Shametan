package jp.gr.java_conf.androtaku.shametan.shametan;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class MainActivity extends Activity {

    public static int menuType = 1;
    public static final int MENU_MAIN = 1;
    public static final int MENU_DRAWLINE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, PlaceholderFragment.newInstance())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        switch(menuType) {
            case MENU_MAIN:
                getMenuInflater().inflate(R.menu.main, menu);
                break;

            case MENU_DRAWLINE:
                getMenuInflater().inflate(R.menu.drawline_menu,menu);
                break;

            default:
        }
        return super.onCreateOptionsMenu(menu);
    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        Button cameraButton,galleryButton;

        public PlaceholderFragment() {

        }

        public static PlaceholderFragment newInstance(){
            PlaceholderFragment fragment = new PlaceholderFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            init(rootView);
            setHasOptionsMenu(true);
            return rootView;
        }

        @Override
        public void onResume(){
            super.onResume();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_settings) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        public void init(View v){
            cameraButton = (Button)v.findViewById(R.id.button);
            cameraButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toCamera();
                }
            });

            galleryButton = (Button)v.findViewById(R.id.galleryButton);
            galleryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toGallery();
                }
            });
        }

        public void toGallery(){
            /*FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.container,GalleryBigFragment.newInstance());
            transaction.addToBackStack("main");
            transaction.commit();*/
            Intent intent = new Intent(getActivity().getApplicationContext(),GetImageFromGalleryActivity.class);
            getActivity().startActivity(intent);
        }

        public void toCamera(){
            /*FragmentManager manger = getFragmentManager();
            FragmentTransaction transaction = manger.beginTransaction();
            transaction.replace(R.id.container,CameraFragment.newInstance());
            transaction.addToBackStack("main");
            transaction.commit();*/
            Intent intent = new Intent(getActivity().getApplicationContext(),GetImageFromCameraActivity.class);
            getActivity().startActivity(intent);
        }
    }
}
