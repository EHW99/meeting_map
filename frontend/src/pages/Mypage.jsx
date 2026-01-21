import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './Mypage.css';
import { useAppContext } from '../AppContext';
import axios from 'axios';
import { API_BASE_URL } from '../constants';
import { timeAgo } from '../utils/timeAgo';
import { formatScheduleDate, formatScheduleDate2 } from '../utils/formatScheduleDate';

const menuItems = [
  { id: 'posts', label: '내 게시물', icon: '📝' },
  { id: 'liked', label: '좋아요/저장한 글', icon: '❤️' },
  { id: 'friends', label: '친구 목록', icon: '👥' },
  { id: 'friendReceived', label: '받은 친구 요청', icon: '📬' },
  { id: 'groups', label: '받은 그룹 초대', icon: '📨' },
  { id: 'schedules', label: '일정', icon: '📅' },
  { id: 'settings', label: '설정', icon: '⚙️' },
];

const Mypage = () => {
  const { user, setUser } = useAppContext();
  const [view, setView] = useState('posts');
  const [avatar, setAvatar] = useState(null);
  const [address, setAddress] = useState("");
  const [nick, setNick] = useState("");
  const [passwd, setPasswd] = useState("");
  const [onlyFriendsCanSeeActivity, setOnlyFriendsCanSeeActivity] = useState(false);
  const [emailNotificationAgree, setEmailNotificationAgree] = useState(false);
  const [deletePassword, setDeletePassword] = useState("");
  const [myPosts, setMyPosts] = useState([]);
  const [likedPosts, setLikedPosts] = useState([]);
  const [savedPosts, setSavedPosts] = useState([]);
  const [newFriend, setNewFriend] = useState({ name: '', email: '' });
  const [friendReceived, setFriendReceived] = useState([]);
  const [groupInvitations, setGroupInvitations] = useState([]);
  const [openScheduleNo, setOpenScheduleNo] = useState(null);

  const navigate = useNavigate();
  const location = useLocation();
  const [searchTerm, setSearchTerm] = useState('');
  const [friends, setFriends] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [settingsTab, setSettingsTab] = useState('profile');

  useEffect(() => {
    fetchMyPosts();
    fetchLikedPosts();
    fetchSavedPosts();
    fetchSchedules();
    fetchFriends();
    fetchFriendReceived();
    fetchGroupInvitations();
  }, []);

  useEffect(() => {
    if (user === null) {
      navigate('/login', { state: { from: location.pathname } });
    }
  }, [user, navigate, location.pathname]);

  useEffect(() => {
    if (user && user.userImg) {
      setAvatar(user.userImg);
      setAddress(user.userAddress);
      setNick(user.userNick);
      setOnlyFriendsCanSeeActivity(user.onlyFriendsCanSeeActivity);
      setEmailNotificationAgree(user.emailNotificationAgree);
    }
  }, [user]);

  const fetchMyPosts = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/boards`, { withCredentials: true });
      setMyPosts(res.data);
    } catch (err) {
      handleError('내 게시글 불러오기 실패:', err);
    }
  };

  const fetchLikedPosts = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/boards/liked`, { withCredentials: true });
      setLikedPosts(res.data);
    } catch (err) {
      handleError('좋아요한 게시글 불러오기 실패:', err);
    }
  };

  const fetchSavedPosts = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/boards/scraped`, { withCredentials: true });
      setSavedPosts(res.data);
    } catch (err) {
      handleError('저장한 게시글 불러오기 실패:', err);
    }
  };

  const fetchSchedules = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/schedules`, { withCredentials: true });
      setSchedules(res.data);
    } catch (err) {
      handleError('일정 불러오기 실패:', err);
    }
  };

  const fetchFriends = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/friends`, { withCredentials: true });
      setFriends(res.data);
    } catch (err) {
      handleError('친구 불러오기 실패:', err);
    }
  };

  const fetchFriendReceived = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/friends/received`, { withCredentials: true });
      setFriendReceived(res.data);
    } catch (err) {
      handleError('받은 친구 요청 불러오기 실패:', err);
    }
  };

  const fetchGroupInvitations = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/invitations`, { withCredentials: true });
      setGroupInvitations(res.data);
    } catch (err) {
      handleError('받은 그룹 초대 불러오기 실패:', err);
    }
  };

  const handleError = (msg, err) => {
    if (err.response?.data?.message) {
      if (err.response.data.message === "로그인이 필요합니다.")
        navigate("/login", { state: { from: location.pathname } });
      else
        alert(err.response.data.message);
    } else {
      console.error(msg, err);
    }
  };

  const handleAvatarChange = async (e) => {
    const formData = new FormData();
    const file = e.target.files[0];

    if (file) {
      formData.append("profileImage", file);

      try {
        const res = await axios.put(`${API_BASE_URL}/user`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
          withCredentials: true
        });
        setUser(res.data);
      } catch (err) {
        handleError('회원 수정 실패:', err);
      }
    }
  };

  const handleProfileChange = async () => {
    const userData = {
      userEmail: null,
      userNick: nick,
      userAddress: address
    };
    const formData = new FormData();
    formData.append("user", new Blob([JSON.stringify(userData)], { type: "application/json" }));

    try {
      const res = await axios.put(`${API_BASE_URL}/user`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
        withCredentials: true
      });
      setUser(res.data);
      alert("프로필 수정 성공!");
    } catch (err) {
      handleError('회원 수정 실패:', err);
    }
  };

  const handlePasswordChange = async () => {
    try {
      await axios.post(`${API_BASE_URL}/user/password`, { userPasswd: passwd }, { withCredentials: true });
      alert("비밀번호 변경 완료");
    } catch (err) {
      handleError('비밀번호 변경 실패:', err);
    }
  };

  const handleOptionChange = async (e, newValue) => {
    const userData = { [e.target.name]: newValue };
    const formData = new FormData();
    formData.append("user", new Blob([JSON.stringify(userData)], { type: "application/json" }));

    try {
      await axios.put(`${API_BASE_URL}/user`, formData, {
        headers: { "Content-Type": "multipart/form-data" },
        withCredentials: true
      });
      if (e.target.name === "onlyFriendsCanSeeActivity") {
        setOnlyFriendsCanSeeActivity(newValue);
      } else if (e.target.name === "emailNotificationAgree") {
        setEmailNotificationAgree(newValue);
      }
    } catch (err) {
      handleError('설정 변경 실패:', err);
    }
  };

  const handleDeleteAccount = async () => {
    if (window.confirm('정말 탈퇴하시겠습니까?')) {
      try {
        await axios.delete(`${API_BASE_URL}/user`, {
          data: { userPasswd: deletePassword },
          withCredentials: true
        });
        alert("탈퇴 처리되었습니다.");
        setUser(null);
        navigate('/');
      } catch (err) {
        handleError('회원 탈퇴 실패:', err);
      }
    }
  };

  const handleUpdateAddress = () => {
    new window.daum.Postcode({
      oncomplete: function (data) {
        setAddress(data.address);
      }
    }).open();
  };

  const handleDeleteLiked = async (postId) => {
    if (window.confirm('해당 글의 좋아요를 취소하시겠습니까?')) {
      try {
        await axios.post(`${API_BASE_URL}/boards/${postId}/like`, null, { withCredentials: true });
        setLikedPosts(likedPosts.filter(post => post.boardNo !== postId));
      } catch (err) {
        handleError('좋아요 취소 실패:', err);
      }
    }
  };

  const handleDeleteSaved = async (postId) => {
    if (window.confirm('해당 글의 스크랩을 취소하시겠습니까?')) {
      try {
        await axios.post(`${API_BASE_URL}/boards/${postId}/scrap`, null, { withCredentials: true });
        setSavedPosts(savedPosts.filter(post => post.boardNo !== postId));
      } catch (err) {
        handleError('스크랩 취소 실패:', err);
      }
    }
  };

  const handleAddFriend = async () => {
    if (!newFriend.name || !newFriend.email) {
      alert('이름, 이메일을 입력해주세요.');
      return;
    }
    try {
      await axios.post(`${API_BASE_URL}/user/friends/add`, {
        opponentNick: newFriend.name,
        opponentEmail: newFriend.email
      }, { withCredentials: true });
      alert("친구 추가 요청을 보냈습니다.");
      setNewFriend({ name: '', email: '' });
    } catch (err) {
      handleError(err, "친구 추가 실패");
    }
  };

  const handleRemoveFriend = async (id) => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      try {
        await axios.delete(`${API_BASE_URL}/user/friends`, {
          data: { friendshipNo: id },
          withCredentials: true
        });
        setFriends(friends.filter(friend => friend.friendshipNo !== id));
      } catch (err) {
        handleError('친구 삭제 실패:', err);
      }
    }
  };

  const toggleSchedule = (scheduleNo) => {
    setOpenScheduleNo(prev => (prev === scheduleNo ? null : scheduleNo));
  };

  const handleDeleteSchedule = async (index) => {
    try {
      await axios.delete(`${API_BASE_URL}/schedules/${schedules[index].scheduleNo}`, { withCredentials: true });
      const updated = [...schedules];
      updated.splice(index, 1);
      setSchedules(updated);
    } catch (err) {
      handleError('스케줄 삭제 실패:', err);
    }
  };

  const handleAcceptFriend = async (friendshipNo, index) => {
    try {
      await axios.post(`${API_BASE_URL}/user/friends/approve`, { friendshipNo }, { withCredentials: true });
      const updated = [...friendReceived];
      updated.splice(index, 1);
      setFriendReceived(updated);
      fetchFriends();
    } catch (err) {
      handleError('친구 요청 수락 실패:', err);
    }
  };

  const handleAcceptInvitation = async (invitationNo, index) => {
    try {
      await axios.post(`${API_BASE_URL}/groups/invitations/${invitationNo}/accept`, null, { withCredentials: true });
      const updated = [...groupInvitations];
      updated.splice(index, 1);
      setGroupInvitations(updated);
    } catch (err) {
      handleError('그룹 초대 수락 실패:', err);
    }
  };

  const handleRejectInvitation = async (invitationNo, index) => {
    try {
      await axios.post(`${API_BASE_URL}/groups/invitations/${invitationNo}/reject`, null, { withCredentials: true });
      const updated = [...groupInvitations];
      updated.splice(index, 1);
      setGroupInvitations(updated);
    } catch (err) {
      handleError('그룹 초대 거절 실패:', err);
    }
  };

  return (
    <div className="mypage">
      {/* Header Section */}
      <div className="mypage-header">
        <div className="mypage-header-content">
          <h1 className="mypage-title">마이페이지</h1>
          <p className="mypage-subtitle">내 정보와 활동을 관리하세요</p>
        </div>
      </div>

      <div className="mypage-layout">
        {/* Sidebar */}
        <aside className="mypage-sidebar">
          {/* Profile Card */}
          <div className="profile-card">
            <div className="avatar-wrapper">
              {avatar ? (
                <img src={avatar} alt="프로필" className="profile-avatar" />
              ) : (
                <img src="/images/default-pro.png" alt="프로필" className="profile-avatar" />
              )}
              <label htmlFor="avatar-upload" className="avatar-upload-btn">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M23 19a2 2 0 01-2 2H3a2 2 0 01-2-2V8a2 2 0 012-2h4l2-3h6l2 3h4a2 2 0 012 2z"/>
                  <circle cx="12" cy="13" r="4"/>
                </svg>
                <input type="file" id="avatar-upload" accept="image/*" onChange={handleAvatarChange} />
              </label>
            </div>
            <h2 className="profile-name">{user ? user.userNick : "로딩 중..."}</h2>
            <p className="profile-email">{user ? user.userEmail : ""}</p>
          </div>

          {/* Navigation Menu */}
          <nav className="sidebar-nav">
            {menuItems.map((item) => (
              <button
                key={item.id}
                className={`nav-item ${view === item.id ? 'active' : ''}`}
                onClick={() => setView(item.id)}
              >
                <span className="nav-icon">{item.icon}</span>
                <span className="nav-label">{item.label}</span>
              </button>
            ))}
          </nav>
        </aside>

        {/* Main Content */}
        <main className="mypage-content">
          {/* My Posts View */}
          {view === 'posts' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7"/>
                    <path d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z"/>
                  </svg>
                  내 게시물
                </h2>
                <span className="count-badge">{myPosts.length}개</span>
              </div>

              {myPosts.length === 0 ? (
                <div className="empty-state">
                  <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <path d="M14 2H6a2 2 0 00-2 2v16a2 2 0 002 2h12a2 2 0 002-2V8z"/>
                    <polyline points="14,2 14,8 20,8"/>
                  </svg>
                  <p>아직 작성한 게시물이 없습니다</p>
                </div>
              ) : (
                <div className="post-grid">
                  {myPosts.map((post) => (
                    <article key={post.boardNo} className="post-card" onClick={() => navigate(`/boards/${post.boardNo}`)}>
                      {post.thumbnailUrl && (
                        <div className="post-thumbnail">
                          <img src={post.thumbnailUrl} alt={post.boardTitle} />
                        </div>
                      )}
                      <div className="post-body">
                        <h3 className="post-title">{post.boardTitle}</h3>
                        <p className="post-meta">{post.categoryName} · {timeAgo(post.boardUpdateDate)}</p>
                        <div className="post-stats">
                          <span>👁 {post.boardViewCount}</span>
                          <span>❤️ {post.boardLike}</span>
                        </div>
                      </div>
                    </article>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Liked/Saved Posts View */}
          {view === 'liked' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M20.84 4.61a5.5 5.5 0 00-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 00-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 000-7.78z"/>
                  </svg>
                  좋아요/저장한 글
                </h2>
              </div>

              {/* Liked Posts */}
              <div className="sub-section">
                <h3 className="sub-title">❤️ 좋아요한 글</h3>
                {likedPosts.length === 0 ? (
                  <p className="empty-text">좋아요한 글이 없습니다.</p>
                ) : (
                  <div className="item-list">
                    {likedPosts.map((post) => (
                      <div key={post.boardNo} className="item-card">
                        {post.thumbnailUrl && (
                          <img src={post.thumbnailUrl} alt={post.boardTitle} className="item-thumb" />
                        )}
                        <div className="item-info">
                          <h4>{post.boardTitle}</h4>
                          <p>{post.categoryName} · {timeAgo(post.boardUpdateDate)}</p>
                        </div>
                        <button className="btn-cancel" onClick={() => handleDeleteLiked(post.boardNo)}>취소</button>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              {/* Saved Posts */}
              <div className="sub-section">
                <h3 className="sub-title">📌 저장한 글</h3>
                {savedPosts.length === 0 ? (
                  <p className="empty-text">저장한 글이 없습니다.</p>
                ) : (
                  <div className="item-list">
                    {savedPosts.map((post) => (
                      <div key={post.boardNo} className="item-card">
                        {post.thumbnailUrl && (
                          <img src={post.thumbnailUrl} alt={post.boardTitle} className="item-thumb" />
                        )}
                        <div className="item-info">
                          <h4>{post.boardTitle}</h4>
                          <p>{post.categoryName} · {timeAgo(post.boardUpdateDate)}</p>
                        </div>
                        <button className="btn-cancel" onClick={() => handleDeleteSaved(post.boardNo)}>취소</button>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* Friends View */}
          {view === 'friends' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 00-3-3.87"/>
                    <path d="M16 3.13a4 4 0 010 7.75"/>
                  </svg>
                  친구 목록
                </h2>
                <span className="count-badge">{friends.length}명</span>
              </div>

              {/* Add Friend Form */}
              <div className="form-card">
                <h3>친구 추가</h3>
                <div className="form-row">
                  <input
                    type="text"
                    placeholder="이름"
                    value={newFriend.name}
                    onChange={(e) => setNewFriend({ ...newFriend, name: e.target.value })}
                  />
                  <input
                    type="email"
                    placeholder="이메일"
                    value={newFriend.email}
                    onChange={(e) => setNewFriend({ ...newFriend, email: e.target.value })}
                  />
                  <button className="btn-primary" onClick={handleAddFriend}>친구 요청</button>
                </div>
              </div>

              {/* Search */}
              <div className="search-box">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <circle cx="11" cy="11" r="8"/>
                  <path d="M21 21l-4.35-4.35"/>
                </svg>
                <input
                  type="text"
                  placeholder="친구 이름 검색"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>

              {/* Friend List */}
              <div className="friend-list">
                {friends
                  .filter(friend => friend.opponentNick.includes(searchTerm))
                  .map((friend) => (
                    <div key={friend.friendshipNo} className="friend-card">
                      <div className="friend-info">
                        <img
                          src={friend.opponentImg || "/images/default-pro.png"}
                          alt="프로필"
                          className="friend-avatar"
                        />
                        <span className="friend-name">{friend.opponentNick}</span>
                      </div>
                      <button className="btn-danger" onClick={() => handleRemoveFriend(friend.friendshipNo)}>삭제</button>
                    </div>
                  ))}
              </div>
            </div>
          )}

          {/* Friend Requests View */}
          {view === 'friendReceived' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
                  받은 친구 요청
                </h2>
                <span className="count-badge">{friendReceived.length}개</span>
              </div>

              {friendReceived.length === 0 ? (
                <div className="empty-state">
                  <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <path d="M4 4h16c1.1 0 2 .9 2 2v12c0 1.1-.9 2-2 2H4c-1.1 0-2-.9-2-2V6c0-1.1.9-2 2-2z"/>
                    <polyline points="22,6 12,13 2,6"/>
                  </svg>
                  <p>받은 친구 요청이 없습니다</p>
                </div>
              ) : (
                <div className="request-list">
                  {friendReceived.map((friend, index) => (
                    <div key={index} className="request-card">
                      <div className="request-info">
                        <img
                          src={friend.opponentImg || "/images/default-pro.png"}
                          alt="프로필"
                          className="request-avatar"
                        />
                        <div>
                          <strong>{friend.opponentNick}</strong>
                          <p>친구 요청을 보냈습니다</p>
                        </div>
                      </div>
                      <button className="btn-primary" onClick={() => handleAcceptFriend(friend.friendshipNo, index)}>수락</button>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Group Invitations View */}
          {view === 'groups' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                    <path d="M23 21v-2a4 4 0 00-3-3.87"/>
                    <path d="M16 3.13a4 4 0 010 7.75"/>
                  </svg>
                  받은 그룹 초대
                </h2>
                <span className="count-badge">{groupInvitations.length}개</span>
              </div>

              {groupInvitations.length === 0 ? (
                <div className="empty-state">
                  <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/>
                    <circle cx="9" cy="7" r="4"/>
                  </svg>
                  <p>받은 그룹 초대가 없습니다</p>
                </div>
              ) : (
                <div className="invitation-list">
                  {groupInvitations.map((group, index) => (
                    <div key={index} className="invitation-card">
                      <div className="invitation-info">
                        <div className="invitation-icon">👥</div>
                        <div>
                          <strong>{group.groupTitle}</strong>
                          <p>{group.senderNick}님이 초대함</p>
                        </div>
                      </div>
                      <div className="invitation-actions">
                        <button className="btn-primary" onClick={() => handleAcceptInvitation(group.invitationNo, index)}>수락</button>
                        <button className="btn-secondary" onClick={() => handleRejectInvitation(group.invitationNo, index)}>거절</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* Schedules View */}
          {view === 'schedules' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                  일정 관리
                </h2>
                <span className="count-badge">{schedules.length}개</span>
              </div>

              {schedules.length === 0 ? (
                <div className="empty-state">
                  <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                    <rect x="3" y="4" width="18" height="18" rx="2" ry="2"/>
                    <line x1="16" y1="2" x2="16" y2="6"/>
                    <line x1="8" y1="2" x2="8" y2="6"/>
                    <line x1="3" y1="10" x2="21" y2="10"/>
                  </svg>
                  <p>등록된 일정이 없습니다</p>
                </div>
              ) : (
                <div className="schedule-list">
                  {schedules.map((item, index) => {
                    const isOpen = item.scheduleNo === openScheduleNo;
                    return (
                      <div key={index} className={`schedule-card ${isOpen ? 'expanded' : ''}`}>
                        <div className="schedule-header" onClick={() => toggleSchedule(item.scheduleNo)}>
                          <div className="schedule-info">
                            <div className="schedule-date">
                              <span className="date-icon">📅</span>
                              <span>{formatScheduleDate(item.details[0].scheduleStartTime)}</span>
                              <span className="date-separator">~</span>
                              <span>{formatScheduleDate2(item.details[item.details.length - 1].scheduleEndTime)}</span>
                            </div>
                            <h4 className="schedule-name">{item.scheduleName}</h4>
                          </div>
                          <div className="schedule-actions">
                            <button className="btn-icon-danger" onClick={(e) => { e.stopPropagation(); handleDeleteSchedule(index); }}>
                              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                <polyline points="3 6 5 6 21 6"/>
                                <path d="M19 6v14a2 2 0 01-2 2H7a2 2 0 01-2-2V6m3 0V4a2 2 0 012-2h4a2 2 0 012 2v2"/>
                              </svg>
                            </button>
                            <svg
                              className={`chevron ${isOpen ? 'open' : ''}`}
                              width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"
                            >
                              <polyline points="6 9 12 15 18 9"/>
                            </svg>
                          </div>
                        </div>
                        {isOpen && (
                          <div className="schedule-details">
                            <h5>세부 일정</h5>
                            {item.details.map((d, i) => (
                              <div key={i} className="detail-item">
                                <div className="detail-time">
                                  {formatScheduleDate2(d.scheduleStartTime)} ~ {formatScheduleDate2(d.scheduleEndTime)}
                                </div>
                                <div className="detail-content">{d.scheduleContent}</div>
                              </div>
                            ))}
                          </div>
                        )}
                      </div>
                    );
                  })}
                </div>
              )}
            </div>
          )}

          {/* Settings View */}
          {view === 'settings' && (
            <div className="content-section">
              <div className="section-header">
                <h2 className="section-title">
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="3"/>
                    <path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-2 2 2 2 0 01-2-2v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83 0 2 2 0 010-2.83l.06-.06a1.65 1.65 0 00.33-1.82 1.65 1.65 0 00-1.51-1H3a2 2 0 01-2-2 2 2 0 012-2h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 010-2.83 2 2 0 012.83 0l.06.06a1.65 1.65 0 001.82.33H9a1.65 1.65 0 001-1.51V3a2 2 0 012-2 2 2 0 012 2v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 0 2 2 0 010 2.83l-.06.06a1.65 1.65 0 00-.33 1.82V9a1.65 1.65 0 001.51 1H21a2 2 0 012 2 2 2 0 01-2 2h-.09a1.65 1.65 0 00-1.51 1z"/>
                  </svg>
                  설정
                </h2>
              </div>

              <div className="settings-layout">
                {/* Settings Tabs */}
                <div className="settings-tabs">
                  {[
                    { id: 'profile', label: '프로필 수정', icon: '👤' },
                    ...(user && user.userId?.substr(0, 6) !== "kakao_" ? [{ id: 'account', label: '계정 정보', icon: '🔐' }] : []),
                    { id: 'privacy', label: '개인정보 설정', icon: '🔒' },
                    { id: 'notifications', label: '알림 설정', icon: '🔔' },
                    ...(user && user.userId?.substr(0, 6) !== "kakao_" ? [{ id: 'withdraw', label: '회원 탈퇴', icon: '⚠️' }] : []),
                  ].map((tab) => (
                    <button
                      key={tab.id}
                      className={`settings-tab ${settingsTab === tab.id ? 'active' : ''}`}
                      onClick={() => setSettingsTab(tab.id)}
                    >
                      <span>{tab.icon}</span>
                      <span>{tab.label}</span>
                    </button>
                  ))}
                </div>

                {/* Settings Content */}
                <div className="settings-content">
                  {settingsTab === 'profile' && (
                    <div className="settings-panel">
                      <h3>프로필 수정</h3>
                      <p className="settings-desc">주소 및 닉네임을 변경할 수 있습니다.</p>
                      <div className="form-group">
                        <label>주소</label>
                        <input type="text" value={address} placeholder="주소를 검색하세요" onClick={handleUpdateAddress} readOnly />
                      </div>
                      <div className="form-group">
                        <label>닉네임</label>
                        <input type="text" value={nick} placeholder="새 닉네임" onChange={(e) => setNick(e.target.value)} />
                      </div>
                      <button className="btn-primary" onClick={handleProfileChange}>저장</button>
                    </div>
                  )}

                  {settingsTab === 'account' && (
                    <div className="settings-panel">
                      <h3>계정 정보</h3>
                      <div className="info-row">
                        <span className="info-label">이메일</span>
                        <span className="info-value">{user ? user.userEmail : "로딩 중..."}</span>
                      </div>
                      <div className="form-group">
                        <label>새 비밀번호</label>
                        <input type="password" value={passwd} placeholder="새 비밀번호 입력" onChange={(e) => setPasswd(e.target.value)} />
                      </div>
                      <button className="btn-primary" onClick={handlePasswordChange}>변경</button>
                    </div>
                  )}

                  {settingsTab === 'privacy' && (
                    <div className="settings-panel">
                      <h3>개인정보 설정</h3>
                      <label className="toggle-option">
                        <input
                          type="checkbox"
                          name="onlyFriendsCanSeeActivity"
                          checked={onlyFriendsCanSeeActivity}
                          onChange={(e) => handleOptionChange(e, !onlyFriendsCanSeeActivity)}
                        />
                        <span className="toggle-label">내 활동을 친구에게만 공개</span>
                      </label>
                    </div>
                  )}

                  {settingsTab === 'notifications' && (
                    <div className="settings-panel">
                      <h3>알림 설정</h3>
                      <label className="toggle-option">
                        <input
                          type="checkbox"
                          name="emailNotificationAgree"
                          checked={emailNotificationAgree}
                          onChange={(e) => handleOptionChange(e, !emailNotificationAgree)}
                        />
                        <span className="toggle-label">이메일 알림 수신 동의</span>
                      </label>
                    </div>
                  )}

                  {settingsTab === 'withdraw' && (
                    <div className="settings-panel danger-zone">
                      <h3>회원 탈퇴</h3>
                      <p className="settings-desc warning">계정을 삭제하면 모든 데이터가 영구적으로 삭제됩니다.</p>
                      <div className="form-group">
                        <label>비밀번호 확인</label>
                        <input type="password" value={deletePassword} placeholder="비밀번호" onChange={(e) => setDeletePassword(e.target.value)} />
                      </div>
                      <button className="btn-danger" onClick={handleDeleteAccount}>탈퇴하기</button>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default Mypage;
