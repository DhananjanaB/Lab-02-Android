
package com.example.uidesignlab;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<UserRecord> users = new ArrayList<>();

    // Interface to communicate with UsersActivity
    public interface OnUserClickListener {
        void onUserClick(UserRecord user);
    }

    private OnUserClickListener listener;

    // Constructor
    public UserAdapter(OnUserClickListener listener) {
        this.listener = listener;
    }

    // Store new user list
    public void setUsers(List<UserRecord> newUsers) {
        users = newUsers;
        notifyDataSetChanged();
    }

    // ViewHolder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        TextView txtItem;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            txtItem = itemView.findViewById(R.id.txtItem);
        }
    }

    // Create a new row
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);

        return new UserViewHolder(view);
    }

    // Display user data and handle row click
    @Override
    public void onBindViewHolder(
            @NonNull UserViewHolder holder, int position) {

        UserRecord user = users.get(position);

        holder.txtItem.setText(user.id + ". " + user.name);

        // Detect row click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onUserClick(user);
            }
        });
    }

    // Return number of users
    @Override
    public int getItemCount() {
        return users.size();
    }
}
