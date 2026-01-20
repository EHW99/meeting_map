import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import './Mypage.css';
import { getPostsFromLocal, removePostFromLocal } from '../utils/storage';
import { members } from '../data/members';
import { useAppContext } from '../AppContext'
import axios from 'axios';
import { API_BASE_URL } from '../constants';
import { timeAgo } from '../utils/timeAgo';
import { formatScheduleDate, formatScheduleDate2 } from '../utils/formatScheduleDate'

const mockUser = {
  id: '홍길동',
  avatarUrl: '/images/defaultPro.png',
};

const Mypage = () => {
  const { user, setUser } = useAppContext();
  const [view, setView] = useState('home');
  const [avatar, setAvatar] = useState(null); // 상태로 관리
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
  const [newSchedule, setNewSchedule] = useState({ title: '', date: '' });
  const [schedules, setSchedules] = useState([]);
  const [settingsTab, setSettingsTab] = useState('profile');

  // 게시글 가져오는 api 호출
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
      navigate('/login', { state: { from: location.pathname } }); // 로그인 페이지로 이동
    }
  }, [user, navigate, location.pathname]);

  // user가 바뀔 때 avatar 초기화
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
      setMyPosts(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('내 게시글 불러오기 실패:', err);
    }
  };

  const fetchLikedPosts = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/boards/liked`, { withCredentials: true });
      setLikedPosts(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('좋아요한 게시글 불러오기 실패:', err);
    }
  };

  const fetchSavedPosts = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/boards/scraped`, { withCredentials: true });
      setSavedPosts(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('저장한 게시글 불러오기 실패:', err);
    }
  };

  const fetchSchedules = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/schedules`, { withCredentials: true });
      setSchedules(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('일정 불러오기 실패:', err);
    }
  };

  const fetchFriends = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/friends`, { withCredentials: true });
      setFriends(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('친구 불러오기 실패:', err);
    }
  };

  const fetchFriendReceived = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/user/friends/received`, { withCredentials: true });
      setFriendReceived(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('받은 친구 요청 불러오기 실패:', err);
    }
  };

  const fetchGroupInvitations = async () => {
    try {
      const res = await axios.get(`${API_BASE_URL}/groups/invitations`, { withCredentials: true });
      setGroupInvitations(res.data); // 서버 응답 구조에 맞게 조정
    } catch (err) {
      handleError('받은 그룹 초대 불러오기 실패:', err);
    }
  };

  const handleError = (msg, err) => {
    if (err.response?.data?.message) {
        if (err.response.data.message === "로그인이 필요합니다.")
          navigate("/login", { state: { from: location.pathname } }); // 로그인 페이지로 이동
        else
          alert(err.response.data.message);
      } else {
        console.error(msg, err);
      }
  }

  const handleAvatarChange = async (e) => {
    const formData = new FormData();
    const file = e.target.files[0];

    // 파일이 있으면 formData에 추가
    if (file) {
      formData.append("profileImage", file);

      try {
        const res = await axios.put(`${API_BASE_URL}/user`, formData, {
          headers: {
              "Content-Type": "multipart/form-data",
          },
          withCredentials: true
        });
        const newUserData = res.data;
        setUser(newUserData);
      } catch (err) {
        handleError('회원 수정 실패:', err);
      }
    }
  };

  const handleProfileChange = async (e) => {
    const userData = {
      "userEmail": null,
      "userNick": nick,
      "userAddress": address
    };
    const formData = new FormData();

    // JSON 데이터를 Blob으로 만들어서 추가
    formData.append(
        "user",
        new Blob([JSON.stringify(userData)], { type: "application/json" })
    );

    try {
      const res = await axios.put(`${API_BASE_URL}/user`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
        withCredentials: true
      });
      const newUserData = res.data;
      setUser(newUserData);
      alert("프로필 수정 성공!");
    } catch (err) {
      handleError('회원 수정 실패:', err);
    }
  };

  const handlePasswordChange = async (e) => {
    try {
      await axios.post(`${API_BASE_URL}/user/password`, {
        "userPasswd": passwd
      }, { withCredentials: true });
      alert("비밀번호 변경 완료");
    } catch (err) {
      handleError('비밀번호 변경 실패:', err);
    }
  }

  const handleOptionChange = async (e, newValue) => {
    const userData = {
      [e.target.name]: newValue
    };

    const formData = new FormData();

    // JSON 데이터를 Blob으로 만들어서 추가
    formData.append(
        "user",
        new Blob([JSON.stringify(userData)], { type: "application/json" })
    );
    
    try {
      await axios.put(`${API_BASE_URL}/user`, formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        },
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
  }

  const handleKeyDown = (e) => {
    if (e.key !== 'Enter') return;

    const name = e.target.name;

    switch (name) {
      case 'nickname':
        handleProfileChange(e);
        break;
      case 'password':
        handlePasswordChange(e);
        break;
      default:
        break;
    }
  };

  const handleDeleteAccount = async () => {
    if (window.confirm('정말 탈퇴하시겠습니까?')) {
      try {
        await axios.delete(`${API_BASE_URL}/user`, {
          data: {
            "userPasswd": deletePassword
          },
          withCredentials: true
        });
        alert("탈퇴 처리되었습니다.")
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
        setAddress(data.address); // 주소 상태가 있다면 업데이트
      }
    }).open();
  };

  const handleDeleteLiked = async (postId) => {
    if (window.confirm('해당 글의 좋아요를 취소하시겠습니까?')) {
      try {
        await axios.post(`${API_BASE_URL}/boards/${postId}/like`, null, {
          withCredentials: true
        });
        setLikedPosts(likedPosts.filter(post => post.boardNo !== postId));
      } catch (err) {
        handleError('좋아요 취소 실패:', err);
      }
    }
  };

  const handleDeleteSaved = async (postId) => {
    if (window.confirm('해당 글의 스크랩을 취소하시겠습니까?')) {
      try {
        await axios.post(`${API_BASE_URL}/boards/${postId}/scrap`, null, {
          withCredentials: true
        });
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
      alert("친구 추가 요청을 보냈습니다.")
    } catch (err) {
      handleError(err, "친구 추가 실패");
    }
  };

  const handleRemoveFriend = async (id) => {
    if (window.confirm('정말 삭제하시겠습니까?')) {
      try {
        await axios.delete(`${API_BASE_URL}/user/friends`, {
          data: {
            friendshipNo: id
          },
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

  const handleAddSchedule = (e) => {
    e.preventDefault();
    setSchedules([...schedules, newSchedule]);
    setNewSchedule({ title: '', date: '' });
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
      await axios.post(`${API_BASE_URL}/user/friends/approve`, {
        friendshipNo
      }, { withCredentials: true });
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
    <div className="mypage-container">
      <aside className="mypage-sidebar">
        <div className="profile-section">
          <div className="avatar-wrapper">
            {avatar !== null
              ? <img src={avatar} alt="프로필 사진" className="profile-avatar" />
              : <img src="/images/default-pro.png" alt="프로필 사진" className="profile-avatar" />}
            <label htmlFor="avatar-upload" className="avatar-upload-btn">📷
              <input type="file" id="avatar-upload" accept="image/*" onChange={handleAvatarChange} />
            </label>
          </div>
          <div className="user-name">{user ? user.userNick : "로딩 중..."}</div>
        </div>
        <nav className="menu-section">
          <ul className="menu-list">
            <li onClick={() => setView('posts')}>내 게시물</li>
            <li onClick={() => setView('liked')}>좋아요/저장한 글</li>
            <li onClick={() => setView('friends')}>친구목록</li>
            <li onClick={() => setView('friendReceived')}>받은 친구 요청</li>
            <li onClick={() => setView('groups')}>받은 그룹 초대</li>
            <li onClick={() => setView('schedules')}>일정</li>
            <li onClick={() => setView('settings')}>설정</li>
          </ul>
        </nav>
      </aside>

      <main className="mypage-content">
        {view === 'home' && (
            <div className="welcome-text">
              {user ? `${user.userNick}님 반갑습니다!` : "로딩 중..."}
            </div>
        )}
        {view === 'posts' && (
          <div>
            <h2>내 게시물</h2>
            <div className="post-list">
              {myPosts.map((post) => (
                <div key={post.boardNo} className="post-card" onClick={() => navigate(`/boards/${post.boardNo}`)}>
                  {post.thumbnailUrl ? <img src={post.thumbnailUrl} alt={post.boardTitle} className="post-thumb" /> : <></>}
                  <div className="post-info">
                    <h3 style={{margin: 0}}>{post.boardTitle}</h3>
                    <p style={{margin: 0}}>{post.categoryName} · {timeAgo(post.boardUpdateDate)}</p>
                    <p style={{margin: 0}}>조회수 {post.boardViewCount} · 좋아요 {post.boardLike}</p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}
        {view === 'liked' && (
          <div className="liked-saved-wrapper">
            <h2>📌 좋아요/저장한 글</h2>

            {/* 좋아요 섹션 */}
            <section className="liked-saved-section">
              <h3 className="section-subtitle">📌 좋아요한 글</h3>
              {likedPosts.length === 0 ? (
                <p className="empty-text">좋아요한 글이 없습니다.</p>
              ) : (
                <ul className="liked-posts-list">
                  {likedPosts.map((post) => (
                    <li key={post.boardNo} className="liked-post-item">
                      {post.thumbnailUrl ? <img src={post.thumbnailUrl} alt={post.boardTitle} className="post-thumb" /> : <></>}
                      <div>
                        <h3 style={{margin: 0}}>{post.boardTitle}</h3>
                        <p style={{margin: 0}}>{post.categoryName} · {timeAgo(post.boardUpdateDate)}</p>
                        <button onClick={() => handleDeleteLiked(post.boardNo)}>취소</button>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </section>

            {/* 저장한 글 섹션 */}
            <section className="liked-saved-section">
              <h3 className="section-subtitle">📌 저장한 글</h3>
              {savedPosts.length === 0 ? (
                <p className="empty-text">저장한 글이 없습니다.</p>
              ) : (
                <ul className="liked-posts-list">
                  {savedPosts.map((post) => (
                    <li key={post.boardNo} className="liked-post-item">
                      {post.thumbnailUrl ? <img src={post.thumbnailUrl} alt={post.boardTitle} className="post-thumb" /> : <></>}
                      <div>
                        <h3 style={{margin: 0}}>{post.boardTitle}</h3>
                        <p style={{margin: 0}}>{post.categoryName} · {timeAgo(post.boardUpdateDate)}</p>
                        <button onClick={() => handleDeleteSaved(post.boardNo)}>취소</button>
                      </div>
                    </li>
                  ))}
                </ul>
              )}
            </section>
          </div>
        )}


        {view === 'friends' && (
          <div className="p-4">
            <h2 className="text-xl font-semibold mb-4">친구 목록</h2>

            <div className="add-member-form">
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
                <button onClick={handleAddFriend}>친구 요청</button>
              </div>

            {/* 검색창 */}
            <input
              type="text"
              placeholder="친구 이름 검색"
              className="friend-search-input"
              onChange={(e) => setSearchTerm(e.target.value)}
            />

            {/* 친구 목록 카드 */}
            <div className="friend-list">
              {friends
                .filter(friend => friend.opponentNick.includes(searchTerm))
                .map((friend) => (
                  <div key={friend.friendshipNo} className="friend-card">
                    <div className="friend-info">
                      {friend.opponentImg
                        ? <img src={friend.opponentImg} alt="프로필" className="friend-avatar" />
                        : <img src="/images/default-pro.png" alt="프로필" className="friend-avatar" />}
                      <div className="friend-details">
                        <p className="friend-name">{friend.opponentNick}</p>
                        {/*<p className="friend-status">{friend.statusMessage}</p>*/}
                      </div>
                    </div>
                    <button className="friend-delete-btn" onClick={() => handleRemoveFriend(friend.friendshipNo)}>
                      삭제
                    </button>
                  </div>

                ))}
            </div>
          </div>
        )}

        {view === 'friendReceived' && (
          <div className="p-4">
            <h2 className="text-xl font-semibold mb-4">받은 친구 요청 관리</h2>

            {/* 받은 친구 요청 목록 */}
            <ul className="friend-list">
              {friendReceived.map((friend, index) => (
                <div key={index} className="friend-card">
                  <div className="friend-info">
                    {friend.opponentImg
                        ? <img src={friend.opponentImg} alt="프로필" className="friend-avatar" />
                        : <img src="/images/default-pro.png" alt="프로필" className="friend-avatar" />}
                    <strong>{friend.opponentNick} 의 친구 요청</strong>
                  </div>
                  <button onClick={() => handleAcceptFriend(friend.friendshipNo, index)}>수락</button>
                </div>
              ))}
            </ul>
          </div>
        )}

        {view === 'groups' && (
          <div className="p-4">
            <h2 className="text-xl font-semibold mb-4">받은 그룹 초대 관리</h2>

            {/* 그룹 초대 목록 */}
            <ul className="group-list">
              {groupInvitations.map((group, index) => (
                <li key={index} className="group-card">
                  <div className="group-info">
                    <strong>{group.groupTitle}</strong>
                    <strong>{group.senderNick}이(가) 초대함</strong>
                    <button onClick={() => handleAcceptInvitation(group.invitationNo, index)}>수락</button>
                    <button onClick={() => handleRejectInvitation(group.invitationNo, index)}>거절</button>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        )}

        {view === 'schedules' && (
          <div className="p-4">
            <h2 className="text-xl font-semibold mb-4">일정 관리</h2>

            {/* 일정 추가 폼 */}
            {/*<form onSubmit={handleAddSchedule} className="schedule-form">
              <input
                type="text"
                placeholder="일정 제목"
                value={newSchedule.title}
                onChange={(e) => setNewSchedule({ ...newSchedule, title: e.target.value })}
                required
              />
              <input
                type="date"
                value={newSchedule.date}
                onChange={(e) => setNewSchedule({ ...newSchedule, date: e.target.value })}
                required
              />
              <button type="submit">추가</button>
            </form>*/}

            {/* 일정 목록 */}
            <ul className="schedule-list">
              {schedules.map((item, index) => {
                const isOpen = item.scheduleNo === openScheduleNo;
                return (
                <li key={index} className="schedule-item" onClick={() => toggleSchedule(item.scheduleNo)}>
                  <div>
                    <strong>{formatScheduleDate(item.details[0].scheduleStartTime)}</strong> ~
                    <strong> {formatScheduleDate2(item.details[item.details.length - 1].scheduleEndTime)}</strong>
                  </div>
                  <div>{item.scheduleName}</div>
                  <button onClick={() => handleDeleteSchedule(index)}>삭제</button>
                  {isOpen && (
                    <>
                      <div className="schedule-details">
                        <h3>세부 내용</h3>
                        {item.details.map((d) => (
                          <div style={{"padding-bottom": "10px"}}>
                            <div>
                              <strong>{formatScheduleDate2(d.scheduleStartTime)}</strong> ~
                              <strong> {formatScheduleDate2(d.scheduleEndTime)}</strong>
                            </div>
                            <div>{d.scheduleContent}</div>
                          </div>
                      ))}
                      </div>
                    </>
                  )}
                </li>
              )})}
            </ul>
          </div>
        )}

        {view === 'settings' && (
          <div className="settings-page">
            <div className="settings-menu">
              <ul>
                {['profile', 'account', 'privacy', 'notifications', 'withdraw'].map(tab => (
                  <li
                    key={tab}
                    className={settingsTab === tab ? 'active' : ''}
                    onClick={() => setSettingsTab(tab)}
                  >
                    {tab === 'profile' && '프로필 수정'}
                    {tab === 'account' && user && user.userId.substr(0, 6) !== "kakao_" && '계정 정보'}
                    {tab === 'privacy' && '개인정보 설정'}
                    {tab === 'notifications' && '알림 설정'}
                    {tab === 'withdraw' && user && user.userId.substr(0, 6) !== "kakao_" && '회원 탈퇴'}
                  </li>
                ))}
              </ul>
            </div>
            <div className="settings-content">
              {settingsTab === 'profile' && (
                <div>
                  <h3>프로필 수정</h3>
                  <p>주소 및 닉네임을 변경할 수 있습니다.</p>
                  <input type="text" value={address} placeholder="새 주소 입력" onClick={handleUpdateAddress} readOnly />
                  <input type="text" name="nickname" value={nick} placeholder="새 닉네임 입력" onChange={(e) => setNick(e.target.value)} onKeyDown={handleKeyDown} />
                  <button onClick={handleProfileChange}>저장</button>
                </div>
              )}
              {settingsTab === 'account' && (
                <div>
                  <h3>계정 정보</h3>
                  <p>이메일: {user ? user.userEmail : "로딩 중..."}</p>
                  <input type="password" name="password" value={passwd} placeholder="새 비밀번호 입력" onChange={(e) => setPasswd(e.target.value)} onKeyDown={handleKeyDown} />
                  <button onClick={handlePasswordChange}>변경</button>
                </div>
              )}
              {settingsTab === 'privacy' && (
                <div>
                  <h3>개인정보 설정</h3>
                  <label>
                    <input type="checkbox" name="onlyFriendsCanSeeActivity" checked={onlyFriendsCanSeeActivity} onChange={(e) => handleOptionChange(e, !onlyFriendsCanSeeActivity)} /> 내 활동을 친구에게만 공개
                  </label>
                </div>
              )}
              {settingsTab === 'notifications' && (
                <div>
                  <h3>알림 설정</h3>
                  <label>
                    <input type="checkbox" name="emailNotificationAgree" checked={emailNotificationAgree} onChange={(e) => handleOptionChange(e, !emailNotificationAgree)} /> 이메일 알림 수신 동의
                  </label>
                  {/*<label>
                    <input type="checkbox" /> 앱 푸시 알림 허용
                  </label>*/}
                </div>
              )}
              {settingsTab === 'withdraw' && (
                <div>
                  <h3>회원 탈퇴</h3>
                  <p>계정을 삭제하려면 비밀번호를 입력해주세요.</p>
                  <input type="password" name="deletePassword" value={deletePassword} placeholder="비밀번호" onChange={(e) => setDeletePassword(e.target.value)} />
                  <button className="danger" onClick={handleDeleteAccount}>탈퇴하기</button>
                </div>
              )}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default Mypage;
