package com.yyscamper.cashnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.yyscamper.cashnote.Util.ValuePair;

import java.util.List;

/**
 * Created by yuanf on 2014-03-29.
 */
public class DashboardExpandableAdapter extends BaseExpandableListAdapter {
    private String[] mGroups;
    private List<ValuePair[]> mChildren;
    private Context mContext;
    private LayoutInflater mInflater;

    public DashboardExpandableAdapter(Context ctx, String[] groups, List<ValuePair[]> children) {
        mContext = ctx;
        mGroups = groups;
        mChildren = children;
        mInflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getGroupCount() {
        return mGroups.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ValuePair[] arr = mChildren.get(groupPosition);
        return arr.length;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ValuePair[] arr = mChildren.get(groupPosition);
        return arr[childPosition];
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition << 16 + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.group_title, null);
        }
        TextView  tv = (TextView)convertView.findViewById(R.id.textViewGroupName);
        tv.setText(mGroups[groupPosition]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.row_with_two_columns, null);
        }
        TextView tvLeft = (TextView)convertView.findViewById(R.id.textViewColumnLeft);
        TextView tvRight = (TextView)convertView.findViewById(R.id.textViewColumnRight);
        ValuePair lfv = (ValuePair)getChild(groupPosition, childPosition);
        if (lfv != null && lfv.Value0 != null && lfv.Value1 != null) {
            tvLeft.setText(lfv.Value0.toString() + "   ");
            tvRight.setText(lfv.Value1.toString());
        }
        else {
            tvLeft.setText("N/A");
            tvRight.setText("");
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return super.isEmpty();
    }
}
