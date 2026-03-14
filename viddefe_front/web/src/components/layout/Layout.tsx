import { Outlet } from "react-router-dom";
import Aside from "./Aside";
import NavBar from "./Navbar";
import { useSSEConnection } from "../../hooks";

export default function Layout() {
  // Initialize SSE connection for authenticated user
  useSSEConnection();

  return (
    <div className="flex h-screen w-screen overflow-hidden">
      <Aside />

      <div className="flex-1 flex flex-col overflow-hidden">
        <NavBar />
        <main className="flex-1 overflow-y-auto bg-gray-50 p-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
