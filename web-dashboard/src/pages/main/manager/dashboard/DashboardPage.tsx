import {useLoggedUser} from "../../../../compositions/use-logged-user.ts";
import {getGreeting} from "../../../../utils/datetime.utils.ts";
import {DashboardStatistics} from "../../../../components/common/DashboardStatistics.tsx";

export function DashboardPage() {
    const { userProfile } = useLoggedUser();

    return (
        <div className="max-w-7xl mx-auto">
            <div className="mb-8">
                <h2 className="text-2xl font-bold text-gray-800">
                    {getGreeting()}，{userProfile?.nickname}
                </h2>
            </div>

            {/* Statistics */}
            <DashboardStatistics />


        </div>
    )
}