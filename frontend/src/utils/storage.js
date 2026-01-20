export const savePostToLocal = (type, post) => {
    const key = type === "like" ? "likedPosts" : "savedPosts";
    const existing = JSON.parse(localStorage.getItem(key)) || [];
    const already = existing.find((item) => item.id === post.id);
    if (!already) {
      localStorage.setItem(key, JSON.stringify([...existing, post]));
    }
  };
  
  export const getPostsFromLocal = (type) => {
    const key = type === "like" ? "likedPosts" : "savedPosts";
    return JSON.parse(localStorage.getItem(key)) || [];
  };
  
  export const removePostFromLocal = (type, postId) => {
    const key = type === "like" ? "likedPosts" : "savedPosts";
    const existing = JSON.parse(localStorage.getItem(key)) || [];
    const updated = existing.filter((item) => item.id !== postId);
    localStorage.setItem(key, JSON.stringify(updated));
  };
  