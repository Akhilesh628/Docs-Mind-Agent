
import {
  Layers,
  LayoutDashboard,
  MessageSquare,
  Database,
  Settings
} from "lucide-react";

function Sidebar({ activePage, setActivePage }) {
  const menuItems = [
    { name: "Dashboard", icon: LayoutDashboard },
    { name: "AI Chat", icon: MessageSquare },
    { name: "Documents", icon: Database }
  ];

  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="logo-icon">
          <Layers size={26} />
        </div>
        <div>
          <h2>Atharva Assistant</h2>
          <span>AI Knowledge Base</span>
        </div>
      </div>

      <div className="sidebar-label">WORKSPACE</div>

      <nav className="sidebar-menu">
        {menuItems.map((item) => {
          const Icon = item.icon;

          return (
            <button
              key={item.name}
              className={
                activePage === item.name
                  ? "menu-item active"
                  : "menu-item"
              }
              onClick={() => setActivePage(item.name)}
            >
              <Icon size={19} />
              <span>{item.name}</span>
            </button>
          );
        })}
      </nav>

      <div className="sidebar-bottom">
        <Settings size={18} />
        <span>Atharva AI Powered</span>
      </div>

      <div className="sidebar-status">
        <span className="status-dot"></span>
        Backend Connected
      </div>
    </aside>
  );
}

export default Sidebar;
