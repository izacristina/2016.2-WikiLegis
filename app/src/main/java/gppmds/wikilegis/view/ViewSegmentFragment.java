package gppmds.wikilegis.view;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import gppmds.wikilegis.R;
import gppmds.wikilegis.controller.BillController;
import gppmds.wikilegis.controller.DataDownloadController;
import gppmds.wikilegis.controller.SegmentController;
import gppmds.wikilegis.exception.BillException;
import gppmds.wikilegis.exception.SegmentException;
import gppmds.wikilegis.exception.UserException;
import gppmds.wikilegis.exception.VotesException;
import gppmds.wikilegis.model.Segment;

public class ViewSegmentFragment extends Fragment implements View.OnClickListener{

    private static Integer segmentId;
    private static Integer billId;
    private TextView likes;
    private TextView dislikes;
    private TextView segmentText;
    private TextView billText;
    private List<Segment> segmentList;
    private SegmentController segmentController;
    private List<Segment> proposalsList = new ArrayList<>();
    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Button proposalButon;
    FloatingActionButton floatingActionButton;
    private ImageView share;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        segmentId = getArguments().getInt("segmentId");
        billId = getArguments().getInt("billId");

        setView(inflater, container);

        recyclerView.setHasFixedSize(true);

        linearLayoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        segmentController = SegmentController.getInstance(getContext());
        segmentList = SegmentController.getAllSegments();

        floatingActionButton = (FloatingActionButton)getActivity().findViewById(R.id.floatingButton);
        floatingActionButton.setVisibility(View.VISIBLE);
        floatingActionButton.setOnClickListener(this);

        settingText();

        TabLayout tabs = (TabLayout) getActivity().findViewById(R.id.tabs);
        tabs.setVisibility(View.GONE);

        DataDownloadController dataDownloadController = DataDownloadController.getInstance(getContext());
        //TODO TESTAR
        if(dataDownloadController.connectionType() < 2){
            Log.d("Entrou aqui no wifi...", "");
            try {
                proposalsList = segmentController.getSegmentsOfBillById(billId+"" ,""+segmentId, true);
                Log.d("Numero de propostas",proposalsList.size()+"");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (BillException e) {
                e.printStackTrace();
            } catch (SegmentException e) {
                e.printStackTrace();
            }
        }else {
            proposalsList = SegmentController.getProposalsOfSegment(segmentList, segmentId);
        }

            RecyclerViewAdapterContent content = new RecyclerViewAdapterContent(proposalsList);
        recyclerView.setAdapter(content);

        return view;
    }

    private void setView(final LayoutInflater inflater, final ViewGroup container) {

        DataDownloadController dataDownloadController =
                DataDownloadController.getInstance(getContext());

        final int WIFI = 0;
        final int MOBILE_3G = 1;
        final int NO_NETWORK = 2;

        int connectionType = dataDownloadController.connectionType();

        if(connectionType == WIFI || connectionType == MOBILE_3G) {
            view = inflater.inflate(R.layout.fragment_view_segment, container, false);
            likes = (TextView) view.findViewById(R.id.textViewNumberLike);
            dislikes = (TextView) view.findViewById(R.id.textViewNumberDislike);
            billText = (TextView) view.findViewById(R.id.titleBill);
            segmentText = (TextView) view.findViewById(R.id.contentSegment);
            share = (ImageView) view.findViewById(R.id.imageViewShare);
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareTextUrl();
                }
            });
        } else if (connectionType == NO_NETWORK){
            view = inflater.inflate(R.layout.fragment_view_segment_offline, container, false);
            billText = (TextView) view.findViewById(R.id.titleBillOffline);
            segmentText = (TextView) view.findViewById(R.id.contentSegmentOffline);
        }

        recyclerView= (RecyclerView) view.findViewById(R.id.recycler_viewSegment);
    }

    private void settingText() {
        try {
            DataDownloadController dataDownloadController = DataDownloadController.getInstance(getContext());
            SegmentController segmentController = SegmentController.getInstance(getContext());
            //TODO TESTAR
            if(dataDownloadController.connectionType() < 2) {
                final Segment SEGMENT =
                        segmentController.getSegmentByIdFromList(segmentId);

                segmentText.setText(SEGMENT.getContent());
                billText.setText(BillController.getBillByIdFromList(billId).getTitle());
                dislikes.setText(DataDownloadController.getNumberOfVotesbySegment(segmentId,false)+"");
                likes.setText(DataDownloadController.getNumberOfVotesbySegment(segmentId,true) +"");
            }else{
                final Segment SEGMENT = SegmentController.getSegmentById(segmentId, getContext());
                segmentText.setText(SEGMENT.getContent());
                billText.setText(BillController.getBillById(billId).getTitle());
            }

        } catch (SegmentException e) {
            e.printStackTrace();
        } catch (BillException e) {
            e.printStackTrace();
        } catch (VotesException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void openFragment(Fragment fragmentToBeOpen){

        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getActivity().getSupportFragmentManager().beginTransaction();

        fragmentTransaction.replace(R.id.view_segment_fragment, fragmentToBeOpen,
                "SUGGEST_PROPOSAL");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.floatingButton){
            SharedPreferences session = PreferenceManager.getDefaultSharedPreferences(getContext());

            if(session.getBoolean(getContext().getString(R.string.is_logged_in), false)){

                Bundle segmentAndBillId = new Bundle();
                segmentAndBillId.putInt("billId", billId);
                segmentAndBillId.putInt("segmentId", segmentId);

                CreateSuggestProposal createSuggestProposal = new CreateSuggestProposal();
                createSuggestProposal.setArguments(segmentAndBillId);

                openFragment(createSuggestProposal);
            }
            else{
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        floatingActionButton.setVisibility(View.INVISIBLE);
    }

    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        String link = getString(R.string.edemocracia_domain) + getString(R.string.edemocracia_bill)
                + billId + getString(R.string.edemocracia_segment) + segmentId;

        share.putExtra(Intent.EXTRA_SUBJECT, "Dê uma olhada nessa proposta de Lei:");
        share.putExtra(Intent.EXTRA_TEXT, link);

        startActivity(Intent.createChooser(share, "Compartilhar no Aplicativo:"));
    }
}

